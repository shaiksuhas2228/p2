const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");

module.exports = {
  output: {
    uniqueName: "shell",
    publicPath: "auto"
  },
  optimization: {
    runtimeChunk: false
  },
  plugins: [
    new ModuleFederationPlugin({
      remotes: {
        feedMfe: "http://localhost:4201/remoteEntry.js",
        profileMfe: "http://localhost:4202/remoteEntry.js",
        chatMfe: "http://localhost:4203/remoteEntry.js"
      },
      shared: {
        "@angular/core": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/common": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/router": { singleton: true, strictVersion: false, requiredVersion: 'auto' }
      }
    })
  ]
};
