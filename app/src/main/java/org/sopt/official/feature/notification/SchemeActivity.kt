package org.sopt.official.feature.notification

import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import org.sopt.official.auth.model.UserStatus
import org.sopt.official.common.util.isExpiredDate
import org.sopt.official.common.util.serializableExtra
import org.sopt.official.feature.home.HomeActivity
import org.sopt.official.feature.notification.enums.DeepLinkType
import org.sopt.official.network.persistence.SoptDataStore
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class SchemeActivity : AppCompatActivity() {
    @Inject
    lateinit var dataStore: SoptDataStore
    private val args by serializableExtra(StartArgs("", ""))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleDeepLink()
    }

    private fun handleDeepLink() {
        val link = args?.link ?: ""
        val linkIntent = when (
            link.contains("http://")
            || link.contains("https://")
        ) {
            true -> checkWebLinkExpiration(link)
            false -> checkDeepLinkExistence(link)

        }

        when (!isTaskRoot) {
            true -> startActivity(linkIntent)
            false -> TaskStackBuilder.create(this).apply {
                if (!isIntentToHome(linkIntent)) addNextIntentWithParentStack(
                    DeepLinkType.getHomeIntent(this@SchemeActivity, UserStatus.of(dataStore.userStatus))
                )
                addNextIntent(linkIntent)
            }.startActivities()
        }
        finish()
    }

    private fun checkWebLinkExpiration(link: String): Intent {
        return when (link.toUri().getQueryParameter("expiredAt")?.isExpiredDate()) {
            true -> DeepLinkType.getHomeIntent(
                this,
                UserStatus.of(dataStore.userStatus),
                DeepLinkType.EXPIRED
            )
            else -> Intent(Intent.ACTION_VIEW, Uri.parse(link))
        }
    }

    private fun checkDeepLinkExistence(link: String?): Intent {
        return when (link.isNullOrBlank()) {
            true -> NotificationDetailActivity.getIntent(
                this,
                NotificationDetailActivity.StartArgs(
                    notificationId = args?.notificationId ?: ""
                )
            )
            false -> DeepLinkType.invoke(link).getIntent(
                this,
                UserStatus.of(dataStore.userStatus),
                link
            )
        }
    }

    private fun isIntentToHome(intent: Intent): Boolean {
        return when (intent.component?.className) {
            HomeActivity::class.java.name -> true
            else -> false
        }
    }

    data class StartArgs(
        val notificationId: String,
        val link: String
    ) : Serializable

    companion object {
        @JvmStatic
        fun getIntent(context: Context, args: StartArgs) =
            Intent(context, SchemeActivity::class.java).apply {
                putExtra("args", args)
            }
    }
}