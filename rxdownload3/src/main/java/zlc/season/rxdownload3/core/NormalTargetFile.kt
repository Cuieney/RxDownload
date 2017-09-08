package zlc.season.rxdownload3.core

import okhttp3.ResponseBody
import okio.Okio
import retrofit2.Response
import zlc.season.rxdownload3.core.DownloadConfig.DOWNLOADING_FILE_SUFFIX
import zlc.season.rxdownload3.helper.isChunked
import java.io.File
import java.io.File.separator


class NormalTargetFile(val mission: RealMission) {
    private val realFilePath = mission.actual.savePath + separator + mission.actual.fileName
    private val downloadFilePath = realFilePath + DOWNLOADING_FILE_SUFFIX

    private val realFile = File(realFilePath)
    private val downloadFile = File(downloadFilePath)

    init {
        val dir = File(mission.actual.savePath)
        if (!dir.exists() || !dir.isDirectory) {
            dir.mkdirs()
        }
    }

    fun ensureFinish(): Boolean {
        return if (realFile.exists()) {
            mission.setStatus(Succeed(realFile.length()))
            true
        } else {
            if (downloadFile.exists()) {
                downloadFile.delete()
            }
            downloadFile.createNewFile()
            false
        }
    }

    fun save(response: Response<ResponseBody>) {
        val respBody = response.body() ?: throw Throwable("Response body is NULL")

        var downloadSize = 0L
        val byteSize = 8192L
        val totalSize = respBody.contentLength()

        val downloading = Downloading(isChunked(response), downloadSize, totalSize)

        respBody.source().use { source ->
            Okio.buffer(Okio.sink(realFile)).use { sink ->
                val buffer = sink.buffer()
                var readLen = source.read(buffer, byteSize)

                while (readLen != -1L) {
                    downloadSize += readLen
                    downloading.downloadSize = downloadSize

                    mission.emitStatus(downloading)
                    readLen = source.read(buffer, byteSize)
                }

                downloadFile.renameTo(realFile)
                mission.emitStatus(Succeed(totalSize))
            }
        }
    }
}