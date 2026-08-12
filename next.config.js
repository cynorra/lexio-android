const fs = require('fs');

// Fix Node.js on Windows libuv issue where readlink on directories returns EISDIR instead of EINVAL
const patchReadlink = (origFn) => {
  return function (...args) {
    const cb = typeof args[args.length - 1] === 'function' ? args.pop() : null;
    if (cb) {
      return origFn.call(this, ...args, (err, res) => {
        if (err && err.code === 'EISDIR') {
          const einval = new Error(`EINVAL: invalid argument, readlink '${args[0]}'`);
          einval.code = 'EINVAL';
          return cb(einval);
        }
        return cb(err, res);
      });
    }
    try {
      return origFn.apply(this, args);
    } catch (err) {
      if (err && err.code === 'EISDIR') {
        const einval = new Error(`EINVAL: invalid argument, readlink '${args[0]}'`);
        einval.code = 'EINVAL';
        throw einval;
      }
      throw err;
    }
  };
};

if (fs.readlink) fs.readlink = patchReadlink(fs.readlink);
if (fs.readlinkSync) fs.readlinkSync = patchReadlink(fs.readlinkSync);
if (fs.promises && fs.promises.readlink) {
  const origPromisesReadlink = fs.promises.readlink;
  fs.promises.readlink = async function (...args) {
    try {
      return await origPromisesReadlink.apply(this, args);
    } catch (err) {
      if (err && err.code === 'EISDIR') {
        const einval = new Error(`EINVAL: invalid argument, readlink '${args[0]}'`);
        einval.code = 'EINVAL';
        throw einval;
      }
      throw err;
    }
  };
}

/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
};

module.exports = nextConfig;
