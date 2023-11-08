const rawRE = /(?:\?|&)raw(?:&|$)/

export function raw({ extensions }) {
  return {
    name: 'raw',

    resolveId(source) {
      if (rawRE.test(source)) {
        return source.split('?')[0]
      }
      return null
    },

    load(id) {
      if (rawRE.test(id)) {
        return id.split('?')[0]
      }

      return null
    },

    transform(code, id) {
      if (extensions.includes(id.split('.').pop())) {
        return {
          code: `export default ${JSON.stringify(code)};`,
          map: { mappings: '' }
        }
      }
    }
  }
}
