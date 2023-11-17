package com.speechify.multiplatformswiftpackage.dsl

import com.speechify.multiplatformswiftpackage.domain.DistributionMode
import com.speechify.multiplatformswiftpackage.domain.DistributionURL

/**
 * DSL to create instance of a [DistributionModeDSL].
 */
class DistributionModeDSL {
    internal var distributionMode: DistributionMode = DistributionMode.Local

    /**
     * The XCFramework will be distributed via the local file system.
     */
    fun local() {
        distributionMode = DistributionMode.Local
    }

    /**
     * The XCFramework will be distributed via a ZIP file that can be downloaded from the [url].
     *
     * @param url where the ZIP file can be downloaded from. E.g. https://example.com/packages/
     */
    fun remote(url: String) {
        distributionMode = DistributionMode.Remote(DistributionURL(url))
    }
}
