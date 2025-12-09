const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");

module.exports = {
  output: {
    uniqueName: "feedMfe",
    publicPath: "auto"
  },
  optimization: {
    runtimeChunk: false
  },
  plugins: [
    new ModuleFederationPlugin({
      name: "feedMfe",
      filename: "remoteEntry.js",
      exposes: {
        './Module': './src/app/feed/feed.module.ts'
      },
      shared: {
        "@angular/core": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/common": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/router": { singleton: true, strictVersion: false, requiredVersion: 'auto' }
      }
    })
  ]
};
