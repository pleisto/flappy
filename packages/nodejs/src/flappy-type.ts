export { z as ZodType, ZodSchema } from 'zod'

export type Writable<T> = { -readonly [P in keyof T]: T[P] }

export type IsNever<T> = [T] extends [never] ? true : false
