package com.klask.blueprint

import com.klask.KlaskApp

data public class BlueprintJar(val blueprint: Blueprint, val urlPrefix: String)

public abstract class Blueprint : KlaskApp() {
}
