package com.hoabui.virtualbody3d.ui.onboarding

/**
 * Enum of onboarding slides. Use [pageIndex] for HorizontalPager and [count] for page count.
 */
enum class OnboardingPage(val pageIndex: Int) {
    Slide1(0),
    Slide2(1),
    Slide3(2);

    companion object {
        val count: Int get() = entries.size

        fun fromIndex(index: Int): OnboardingPage? = entries.getOrNull(index)
    }
}
