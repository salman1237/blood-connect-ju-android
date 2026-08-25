package com.deshlet.bloodconnectju.ui.navigation

/** Public (not private to the nav host) so the bottom bar can reference the same route constants. */
object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val ONBOARDING = "onboarding"
    const val HOME = "home"
    const val REQUESTS = "requests"
    const val MY_REQUESTS = "my-requests"
    const val CREATE_REQUEST = "requests/create"
    const val REQUEST_DETAIL = "requests/{id}"
    const val MATCHING_DONORS = "requests/{id}/donors"
    const val DONORS = "donors"
    const val DONOR_DETAIL = "donors/{id}"
    const val LEADERBOARD = "leaderboard"
    const val PROFILE = "profile"
    const val DONATIONS = "donations"
    const val NOTIFICATIONS = "notifications"
    const val SETTINGS = "settings"

    fun requestDetail(id: Int) = "requests/$id"
    fun matchingDonors(id: Int) = "requests/$id/donors"
    fun donorDetail(id: Int) = "donors/$id"
}
