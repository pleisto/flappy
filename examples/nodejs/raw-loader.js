const path = require('path')
const fs = require('fs')
const REQUIRE_PATH_TEST = /(?:\?|&)raw(?:&|$)/

function register() {
  const Module = require('module')
  const orginalLoad = Module._load
  const cwd = process.cwd()
  Module._load = function _load(request, _parent) {
    if (request.match(REQUIRE_PATH_TEST)) {
      return fs.readFileSync(path.join(path.dirname(_parent ? _parent.filename : cwd), request.split('?')[0]), 'utf8')
    }
    return orginalLoad.apply(this, arguments)
  }

  return () => {
    Module._load = orginalLoad
  }
}
register()
