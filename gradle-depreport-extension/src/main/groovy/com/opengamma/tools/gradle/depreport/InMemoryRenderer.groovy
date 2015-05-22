package com.opengamma.tools.gradle.depreport

import com.github.jk1.license.*
import com.github.jk1.license.render.SimpleRenderer
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import com.google.common.collect.Tables
import org.gradle.api.Project

class InMemoryRenderer extends SimpleRenderer
{
	private Project project
	private LicenseReportPlugin.LicenseReportExtension config
	private Table<String, String, String> dependencies = HashBasedTable.create()

	@Override
	void render(ProjectData data)
	{
		project = data.project
		config = project.licenseReport
		renderData(data)
	}

	private void renderData(ProjectData data)
	{
		data.configurations.collect { it.dependencies }.flatten().sort{ it.group }.each {
			renderDependency(it)
		}
	}

	private void renderDependency(ModuleData data)
	{
		String coordinates = "${data.group}:${data.name}:${data.version}"
		Map<String, String> dependency = dependencies.row(coordinates)

		dependency['group'] = data.group
		dependency['name'] = data.name
		dependency['version'] = data.version

		if(data.poms.isEmpty() && data.manifests.isEmpty())
			return

		if( ! data.manifests.isEmpty())
		{
			ManifestData manifest = data.manifests.first()
			if (manifest.url)
				dependency['manifestProjectURL'] = manifest.url

			if (manifest.license)
				dependency['manifestLicense'] = manifest.license
		}


		if( ! data.poms.isEmpty())
		{
			PomData pomData = data.poms.first()
			if (pomData.projectUrl)
				dependency['pomProjectURL'] = pomData.projectUrl

			if(pomData.licenses)
			{
				pomData.licenses.eachWithIndex { License license, Integer count ->
					dependency["pomLicense${count}"] = license.name
					if(license.url)
					{
						dependency["pomLicenseURL${count}"] = license.url
					}
				}
			}
		}
	}

	public Table<String, String, String> getDependencyTable()
	{
		return Tables.unmodifiableTable(dependencies)
	}
}