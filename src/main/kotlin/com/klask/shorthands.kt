package com.klask.shorthands

import com.klask.RedirectResponse

public fun redirect(url: String): RedirectResponse {
    return RedirectResponse(url)
}

