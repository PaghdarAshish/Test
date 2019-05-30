const ExtractTextPlugin = require("extract-text-webpack-plugin");
const path = require('path');

const extractScss = new ExtractTextPlugin('../alfresco-ng2-components/lib/core/prebuilt-themes/[name].css');

module.exports = {

    entry: {
        'adf-blue-orange': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-blue-orange.scss',
        'adf-blue-purple': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-blue-purple.scss',
        'adf-cyan-orange': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-cyan-orange.scss',
        'adf-cyan-purple': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-cyan-purple.scss',
        'adf-green-purple': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-green-purple.scss',
        'adf-green-orange': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-green-orange.scss',
        'adf-pink-bluegrey': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-pink-bluegrey.scss',
        'adf-indigo-pink': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-indigo-pink.scss',
        'adf-purple-green': '../alfresco-ng2-components/lib/core/styles/prebuilt/adf-purple-green.scss'
    },

    resolve: {
        extensions: ['.ts', '.js'],
        modules: [path.resolve(__dirname, '../node_modules')]
    },

    output: {
        filename: '../dist/[name].js'
    },

    module: {
        rules: [{
            test: /\.scss$/,
            use: extractScss.extract([{
                loader: "raw-loader"
            }, {
                loader: "sass-loader"
            }])
        }]
    },
    plugins: [
        extractScss
    ]
};
