package com.sec.myonlinefm.defineview

import android.graphics.Bitmap
import kotlin.jvm.Synchronized

import android.util.Log
import android.util.LruCache

class BitMapCache(maxSize: Int) : LruCache<String?, Bitmap?>(maxSize) {
    override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
        return bitmap!!.getRowBytes() * bitmap.getHeight() / 1024
    }

    override fun entryRemoved(evicted: Boolean, key: String?, oldValue: Bitmap?, newValue: Bitmap?) {
        super.entryRemoved(evicted, key, oldValue, newValue)
    }

    fun clearCache() {
        if (mMemoryCache != null) {
            if (mMemoryCache!!.size() > 0) {
                Log.d("CacheUtils",
                        "mMemoryCache.size() " + mMemoryCache!!.size())
                mMemoryCache!!.evictAll()
                Log.d("CacheUtils", "mMemoryCache.size()" + mMemoryCache!!.size())
            }
            mMemoryCache = null
        }
    }

    @Synchronized
    fun addBitmapToMemoryCache(key: String?, bitmap: Bitmap?) {
        if (mMemoryCache!!.size() == mMemoryCache!!.maxSize()) mMemoryCache!!.evictAll()
        if (mMemoryCache!!.get(key) == null) {
            if (key != null && bitmap != null) mMemoryCache!!.put(key, bitmap)
        } else Log.w("bin1111.yang", "the res is aready exits")
    }

    @Synchronized
    fun getBitmapFromMemCache(key: String?): Bitmap? {
        val bm = mMemoryCache!!.get(key)
        return if (key != null) {
            bm
        } else null
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    @Synchronized
    fun removeImageCache(key: String?) {
        if (key != null) {
            if (mMemoryCache != null) {
                val bm = mMemoryCache!!.remove(key)
                bm?.recycle()
            }
        }
    }

    companion object {
        /**
         * @param maxSize for caches that do not override [.sizeOf], this is
         * the maximum number of entries in the cache. For all other caches,
         * this is the maximum sum of the sizes of the entries in this cache.
         * (int) (Runtime.getRuntime() .maxMemory() / 1024) : device RAM
         */
        private var mMemoryCache: BitMapCache? = null
        fun getInstance(): BitMapCache? {
            return mMemoryCache
        }
    }

    init {
        mMemoryCache = this
    }
}