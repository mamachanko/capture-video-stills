const { createProxyMiddleware } = require('http-proxy-middleware');
const Bundler = require('parcel-bundler');
const express = require('express');

const bundler = new Bundler('./index.html', {
    // Don't cache anything in development
    cache: false,
});

const app = express();
const PORT = process.env.PORT || 3000;

// This route structure is specifc to Netlify functions, so
// if you're setting this up for a non-Netlify project, just use
// whatever values make sense to you.  Probably something like /api/**

app.use(
    '/api/',
    createProxyMiddleware({
        // Your local server
        target: 'http://localhost:8080/',
    })
);

// Pass the Parcel bundler into Express as middleware
app.use(bundler.middleware());

// Run your Express server
app.listen(PORT);
