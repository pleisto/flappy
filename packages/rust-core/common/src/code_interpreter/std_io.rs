use std::io::{self, Write};
/// Forked from https://github.com/wasmerio/wasmer/blob/master/lib/virtual-fs/src/mem_fs/stdio.rs
use virtual_fs::{FsError, Result, VirtualFile};

pub trait BufferedVirtualFile: VirtualFile {
  fn get_buf(&self) -> &Vec<u8>;
}

macro_rules! impl_virtualfile_on_std_streams {
    ($name:ident { readable: $readable:expr, writable: $writable:expr $(,)* }) => {
        /// A wrapper type around the standard I/O stream of the same
        /// name that implements `VirtualFile`.
        #[derive(Debug, Default)]
        pub struct $name {
             buf: Vec<u8>,
        }

        impl $name {
            const fn is_readable(&self) -> bool {
                $readable
            }

            const fn is_writable(&self) -> bool {
                $writable
            }
        }

        impl BufferedVirtualFile for $name {
          fn get_buf(&self) -> &Vec<u8> {
            &self.buf
          }
        }

        #[async_trait::async_trait]
        impl VirtualFile for $name {
            fn last_accessed(&self) -> u64 {
                0
            }

            fn last_modified(&self) -> u64 {
                0
            }

            fn created_time(&self) -> u64 {
                0
            }

            fn size(&self) -> u64 {
                0
            }

            fn set_len(& mut self, _new_size: u64) -> Result<()> {
                Err(FsError::PermissionDenied)
            }

            fn unlink(&mut self) -> futures::future::BoxFuture<'static, Result<()>> {
                Box::pin(async {
                    Ok(())
                })
            }

            fn poll_read_ready(self: std::pin::Pin<&mut Self>, _cx: &mut std::task::Context<'_>) -> std::task::Poll<std::io::Result<usize>> {
                std::task::Poll::Ready(Ok(self.buf.len()))
            }

            fn poll_write_ready(self: std::pin::Pin<&mut Self>, _cx: &mut std::task::Context<'_>) -> std::task::Poll<std::io::Result<usize>> {
                std::task::Poll::Ready(Ok(8192))
            }
        }

        impl_virtualfile_on_std_streams!(impl AsyncSeek for $name);
        impl_virtualfile_on_std_streams!(impl AsyncRead for $name);
        impl_virtualfile_on_std_streams!(impl AsyncWrite for $name);
    };

    (impl AsyncSeek for $name:ident) => {
        impl tokio::io::AsyncSeek for $name {
            fn start_seek(
                self: std::pin::Pin<&mut Self>,
                _position: io::SeekFrom
            ) -> io::Result<()> {
                Err(io::Error::new(
                    io::ErrorKind::PermissionDenied,
                    concat!("cannot seek `", stringify!($name), "`"),
                ))
            }
            fn poll_complete(
                self: std::pin::Pin<&mut Self>,
                _cx: &mut std::task::Context<'_>
            ) -> std::task::Poll<io::Result<u64>>
            {
                std::task::Poll::Ready(
                    Err(io::Error::new(
                        io::ErrorKind::PermissionDenied,
                        concat!("cannot seek `", stringify!($name), "`"),
                    ))
                )
            }
        }
    };

    (impl AsyncRead for $name:ident) => {
        impl tokio::io::AsyncRead for $name {
            fn poll_read(
                mut self: std::pin::Pin<&mut Self>,
                _cx: &mut std::task::Context<'_>,
                buf: &mut tokio::io::ReadBuf<'_>,
            ) -> std::task::Poll<io::Result<()>> {
                std::task::Poll::Ready(
                    if self.is_readable() {
                        let length = buf.remaining().min(self.buf.len());
                        buf.put_slice(&self.buf[..length]);

                        // Remove what has been consumed.
                        self.buf.drain(..length);

                        Ok(())
                    } else {
                        Err(io::Error::new(
                            io::ErrorKind::PermissionDenied,
                            concat!("cannot read from `", stringify!($name), "`"),
                        ))
                    }
                )
            }
        }
    };

    (impl AsyncWrite for $name:ident) => {
        impl tokio::io::AsyncWrite for $name {
            fn poll_write(
                mut self: std::pin::Pin<&mut Self>,
                _cx: &mut std::task::Context<'_>,
                buf: &[u8],
            ) -> std::task::Poll<io::Result<usize>> {
                std::task::Poll::Ready(
                    if self.is_writable() {
                        self.buf.write(buf)
                    } else {
                        Err(io::Error::new(
                            io::ErrorKind::PermissionDenied,
                            concat!("cannot write to `", stringify!($name), "`"),
                        ))
                    }
                )
            }

            fn poll_flush(
                mut self: std::pin::Pin<&mut Self>,
                _cx: &mut std::task::Context<'_>
            ) -> std::task::Poll<io::Result<()>> {
                std::task::Poll::Ready(
                    if self.is_writable() {
                        self.buf.flush()
                    } else {
                        Err(io::Error::new(
                            io::ErrorKind::PermissionDenied,
                            concat!("cannot flush `", stringify!($name), "`"),
                        ))
                    }
                )
            }

            fn poll_shutdown(
                self: std::pin::Pin<&mut Self>,
                _cx: &mut std::task::Context<'_>
            ) -> std::task::Poll<io::Result<()>> {
                std::task::Poll::Ready(Ok(()))
            }
        }
    };
}

impl_virtualfile_on_std_streams!(Stdin {
  readable: true,
  writable: false,
});
impl_virtualfile_on_std_streams!(Stdout {
  readable: false,
  writable: true,
});
impl_virtualfile_on_std_streams!(Stderr {
  readable: false,
  writable: true,
});
