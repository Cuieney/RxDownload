package zlc.season.rxdownload3.core

import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.internal.operators.maybe.MaybeToPublisher.INSTANCE
import zlc.season.rxdownload3.core.DownloadConfig.MAX_MISSION_NUMBER
import java.util.concurrent.Semaphore


object MissionBox {
    private val semaphore = Semaphore(MAX_MISSION_NUMBER, true)

    private val SET = mutableSetOf<RealMission>()

    fun create(mission: Mission): Flowable<Status> {
        val realMission = SET.find { it.actual == mission }

        return if (realMission != null) {
            realMission.getProcessor()
        } else {
            val new = RealMission(semaphore, mission)
            SET.add(new)
            new.getProcessor()
        }
    }

    fun start(mission: Mission): Maybe<Any> {
        val realMission = SET.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not exists"))

        return realMission.start()
    }

    fun stop(mission: Mission): Maybe<Any> {
        val realMission = SET.find { it.actual == mission } ?:
                return Maybe.error(RuntimeException("Mission not exists"))

        return realMission.stop()
    }

    fun startAll(): Maybe<Any> {
        val arrays = mutableListOf<Maybe<Any>>()
        SET.forEach { arrays.add(it.start()) }
        return Flowable.fromIterable(arrays)
                .flatMap(INSTANCE, true, MAX_MISSION_NUMBER)
                .lastElement()
    }


    fun stopAll(): Maybe<Any> {
        val arrays = mutableListOf<Maybe<Any>>()
        SET.forEach { arrays.add(it.stop()) }
        return Flowable.fromIterable(arrays)
                .flatMap(INSTANCE, true, MAX_MISSION_NUMBER)
                .lastElement()
    }
}