#!/bin/bash
build(){
    local toolchain=$2
    local target=$1

    rustup target add $target
    if [ ! "$toolchain" == "" ]; then
        rustup toolchain add $toolchain
    fi

    cargo build --target=$target -r -j 48
}

set -e 

if [ "$1" == "" ]; then
    
    build aarch64-unknown-linux-gnu


    build x86_64-unknown-linux-gnu
    
    build aarch64-apple-darwin

    build x86_64-pc-windows-msvc
fi