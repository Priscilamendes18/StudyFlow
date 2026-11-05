package br.gov.sp.cps.studyflow.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(NotificationHelper.EXTRA_TITLE) ?: "Lembrete do StudyFlow"
        val message = intent.getStringExtra(NotificationHelper.EXTRA_MESSAGE) ?: "Você tem um compromisso agendado!"
        val id = intent.getIntExtra(NotificationHelper.EXTRA_ID, 1001)

        NotificationHelper.showImmediateNotification(context, title, message, id)
    }
}
