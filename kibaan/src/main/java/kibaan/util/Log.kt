package kibaan.util

import android.util.Log
import kibaan.ios.IntEnumDefault

object Log {

    enum class Level : IntEnumDefault {
        verbose, debug, info, warn, error, none;
    }

    var level = Level.verbose

    /**
     * Send a [.VERBOSE] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun v(tag: String?, msg: String): Int {
        return if (level <= Level.verbose) Log.v(tag, msg) else 0
    }

    /**
     * Send a [.VERBOSE] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun v(tag: String?, msg: String, tr: Throwable?): Int {
        return if (level <= Level.verbose) Log.v(tag, msg, tr) else 0
    }

    /**
     * Send a [.DEBUG] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun d(tag: String?, msg: String): Int {
        return if (level <= Level.debug) Log.d(tag, msg) else 0
    }

    /**
     * Send a [.DEBUG] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun d(tag: String?, msg: String, tr: Throwable?): Int {
        return if (level <= Level.debug) Log.d(tag, msg, tr) else 0
    }

    /**
     * Send an [.INFO] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun i(tag: String?, msg: String): Int {
        return if (level <= Level.info) Log.i(tag, msg) else 0
    }

    /**
     * Send a [.INFO] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun i(tag: String?, msg: String, tr: Throwable?): Int {
        return if (level <= Level.info) Log.i(tag, msg, tr) else 0
    }

    /**
     * Send a [.WARN] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun w(tag: String?, msg: String): Int {
        return if (level <= Level.warn) Log.w(tag, msg) else 0
    }

    /**
     * Send a [.WARN] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun w(tag: String?, msg: String, tr: Throwable?): Int {
        return if (level <= Level.warn) Log.w(tag, msg, tr) else 0
    }

    /**
     * Send a {@link #WARN} log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     *        the class or activity where the log call occurs.
     * @param tr An exception to log
     */
    fun w(tag: String?, tr: Throwable?): Int {
        return if (level <= Level.warn) Log.w(tag, tr) else 0
    }

    /**
     * Send an [.ERROR] log message.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     */
    fun e(tag: String?, msg: String): Int {
        return if (level <= Level.error) Log.e(tag, msg) else 0
    }

    /**
     * Send a [.ERROR] log message and log the exception.
     * @param tag Used to identify the source of a log message.  It usually identifies
     * the class or activity where the log call occurs.
     * @param msg The message you would like logged.
     * @param tr An exception to log
     */
    fun e(tag: String?, msg: String, tr: Throwable?): Int {
        return if (level <= Level.error) Log.e(tag, msg, tr) else 0
    }
}