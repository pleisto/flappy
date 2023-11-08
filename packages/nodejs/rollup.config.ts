import typescript from '@rollup/plugin-typescript'
import { raw } from './rollup/raw'

const config = [
  {
    input: 'src/index.ts',
    output: [
      {
        file: './dist/index.esm.js',
        format: 'es',
        sourcemap: true
      },
      {
        file: './dist/index.cjs.js',
        format: 'cjs',
        sourcemap: true,
        interop: 'auto'
      }
    ],
    plugins: [
      typescript({
        tsconfig: './tsconfig.build.json',
        sourceMap: true
      }),
      raw({ extensions: ['mustache'] })
    ]
  }
]
export default config
