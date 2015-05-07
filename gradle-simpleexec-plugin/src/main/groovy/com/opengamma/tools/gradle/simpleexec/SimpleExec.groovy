/*
 * Copyright (C) 2015 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */

package com.opengamma.tools.gradle.simpleexec

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

class SimpleExec extends DefaultTask
{

    @Input
    Object command

    @Input @Optional
    Map<String, String> environment

    @Input @Optional
    File workingDirectory

    @Input @Optional
    boolean inheritEnvironment = true

    @Input @Optional
    boolean throwOnFailure = true

    ShellExecutor executor
    ShellResult output

    @TaskAction
    void exec()
    {
        if( ! executor)
            executor = new DefaultShellExecutor()

        Map<String, String> effectiveEnvironment = [:]
        File wd = workingDirectory ?: new File(System.properties['user.dir'])

        if(inheritEnvironment)
            effectiveEnvironment.putAll System.getenv()
        if(environment)
            effectiveEnvironment.putAll environment

        output = executor.execute(command, effectiveEnvironment, wd)

        if(output.exitCode != 0 && throwOnFailure)
            throw new GradleException("""\
Error running system command (${command.toString()})
=== STDOUT ===
${output.stdOut}
=== /STDOUT ===
=== STDERR ===
${output.stdErr}
=== /STDERR ===
""")
    }
}
