package com.submanager.app.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.submanager.app.core.notify.NotificationHelper
import com.submanager.app.core.util.DateUtils
import com.submanager.app.data.repository.SubscriptionRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

/**
 * Runs daily. Notifies for subscriptions expiring in 30/15/7/3/1/0 days.
 */
@HiltWorker
class ExpiryCheckWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val subscriptionRepository: SubscriptionRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val subs = subscriptionRepository.observeAllForStats().first()
        val reminderDays = setOf(30L, 15L, 7L, 3L, 1L, 0L)

        subs.forEach { sub ->
            val days = DateUtils.remainingDays(sub.expiryDate)
            if (days in reminderDays) {
                val text = if (days == 0L)
                    "${sub.productName} expires today."
                else
                    "${sub.productName} expires in $days day(s) on ${DateUtils.format(sub.expiryDate)}."
                notificationHelper.notify(
                    id = sub.id.toInt(),
                    title = "Subscription Reminder",
                    text = text
                )
            }
        }
        return Result.success()
    }
}
