const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");

module.exports = {
  output: {
    uniqueName: "chatMfe",
    publicPath: "auto"
  },
  optimization: {
    runtimeChunk: false
  },
  plugins: [
    new ModuleFederationPlugin({
      name: "chatMfe",
      filename: "remoteEntry.js",
      exposes: {
        './Module': './src/app/chat/chat.module.ts'
      },
      shared: {
        "@angular/core": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/common": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/router": { singleton: true, strictVersion: false, requiredVersion: 'auto' }
      }
    })
  ]
};
