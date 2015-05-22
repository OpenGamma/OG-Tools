package com.opengamma.tools.gradle.depreport.task

import com.github.jk1.license.LicenseReportPlugin
import com.github.jk1.license.task.ReportTask
import com.google.common.collect.HashBasedTable
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.common.collect.Table
import com.opengamma.tools.gradle.depreport.InMemoryRenderer
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

class AggregatedCSVDependencyReport extends DefaultTask
{
	File outputFile = new File(project.buildDir, "dependencies.csv")

	AggregatedCSVDependencyReport()
	{
		project.rootProject.afterEvaluate {
			dependsOn project.allprojects*.tasks*.withType(ReportTask)
		}
	}

	@TaskAction
	void writeDependencyReport()
	{
		StringBuilder output = new StringBuilder()
		Multimap<String, Project> projectDependencies = HashMultimap.create()

		Table collectedDependencies = HashBasedTable.create()
		project.subprojects.each { p ->
			LicenseReportPlugin.LicenseReportExtension config = p.extensions.getByType(LicenseReportPlugin.LicenseReportExtension)

			if ( ! (config.renderer instanceof InMemoryRenderer))
				throw new GradleException("Can't produce dependency report",
						new AssertionError("Can only generate aggregated report when in-memory renderer is used"))

			InMemoryRenderer renderer = config.renderer
			collectedDependencies.putAll(renderer.dependencyTable)
			renderer.dependencyTable.rowKeySet().each {
				projectDependencies.get(it).add(p)
			}
		}

		Set<String> columns = new LinkedHashSet<>()
		columns.addAll(["group", "name", "version"])
		columns.addAll(collectedDependencies.columnKeySet().sort())
		columns.add("modules")
		CSVFormat format = CSVFormat.RFC4180.withHeader(columns as String[])
		CSVPrinter printer = new CSVPrinter(output, format)
		collectedDependencies.rowMap().each { k, v ->
			List<String> values = []
			columns.each { c ->
				if(c == "modules")
					values << projectDependencies.get(k).collect { it.name }.join(", ")
				else
					values << v[c]
			}
			printer.printRecord(values)
		}
		printer.close()
		outputFile.text = output.toString()
	}
}
