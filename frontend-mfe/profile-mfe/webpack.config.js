const ModuleFederationPlugin = require("webpack/lib/container/ModuleFederationPlugin");

module.exports = {
  output: {
    uniqueName: "profileMfe",
    publicPath: "auto"
  },
  optimization: {
    runtimeChunk: false
  },
  plugins: [
    new ModuleFederationPlugin({
      name: "profileMfe",
      filename: "remoteEntry.js",
      exposes: {
        './Module': './src/app/profile/profile.module.ts'
      },
      shared: {
        "@angular/core": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/common": { singleton: true, strictVersion: false, requiredVersion: 'auto' },
        "@angular/router": { singleton: true, strictVersion: false, requiredVersion: 'auto' }
      }
    })
  ]
};
