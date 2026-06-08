package hu.bme.aut.resource_server

import io.github.classgraph.ClassGraph
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.test.util.AssertionErrors.fail
import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.File
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

@Tag("coverage")
class BusinessCriticalCoverageTest {
    private fun parseJacocoXml(xmlFile: File): Map<Pair<String, String>, Pair<Long, Long>> {
        val map = mutableMapOf<Pair<String, String>, Pair<Long, Long>>()
        if (!xmlFile.exists()) return map

        val factory = DocumentBuilderFactory.newInstance()
        try {
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false)
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false)
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        } catch (_: Exception) {}
        factory.isXIncludeAware = false
        factory.isNamespaceAware = false

        val builder = factory.newDocumentBuilder()
        builder.setEntityResolver { _, _ -> InputSource(StringReader("")) }

        val document = builder.parse(xmlFile)
        val methods = document.getElementsByTagName("method")
        for (i in 0 until methods.length) {
            val methodElement = methods.item(i) as Element
            val methodName = methodElement.getAttribute("name")
            val classElement = methodElement.parentNode as Element
            val className = classElement.getAttribute("name") // slash-separated
            var covered = 0L
            var missed = 0L
            val counters = methodElement.getElementsByTagName("counter")
            for (j in 0 until counters.length) {
                val counter = counters.item(j) as Element
                if (counter.getAttribute("type") == "METHOD") {
                    covered = counter.getAttribute("covered").toLong()
                    missed = counter.getAttribute("missed").toLong()
                }
            }
            map[Pair(className, methodName)] = Pair(covered, missed)
        }
        return map
    }

    private fun checkCoverageForPackage(
        pkg: String,
        jacocoMap: Map<Pair<String, String>, Pair<Long, Long>>,
        annotationFqcn: String
    ): Pair<Long, Long> {
        var covered = 0L
        var missed = 0L

        ClassGraph()
            .enableClassInfo()
            .enableMethodInfo()
            .enableAnnotationInfo()
            .acceptPackages(pkg)
            .scan()
            .use { scanResult ->
                scanResult.allClasses.forEach { classInfo ->
                    val className = classInfo.name.replace('.', '/')
                    classInfo.methodInfo.forEach { methodInfo ->
                        if (methodInfo.annotationInfo.any { it.name == annotationFqcn }) {
                            val key = Pair(className, methodInfo.name)
                            println("Checking method: ${classInfo.name}.${methodInfo.name} for annotation $annotationFqcn")
                            jacocoMap[key]?.let { (c, m) ->
                                covered += c
                                missed += m
                            }
                        }
                    }
                }
            }

        return Pair(covered, missed)
    }

    @Test
    fun verifyBusinessCriticalCoverage() {
        val threshold = 0.80
        val rawPackages = """
            hu.bme.aut.resource_server.ability,hu.bme.aut.resource_server.authentication,hu.bme.aut.resource_server.game,hu.bme.aut.resource_server.profile,hu.bme.aut.resource_server.profile_calculation,hu.bme.aut.resource_server.profile_snapshot,hu.bme.aut.resource_server.recommended_game,hu.bme.aut.resource_server.recommendation,hu.bme.aut.resource_server.result,hu.bme.aut.resource_server.suggest,hu.bme.aut.resource_server.user,hu.bme.aut.resource_server.user_group,hu.bme.aut.resource_server.utils
        """.trimIndent()
        val packagesToScan = rawPackages
            ?.split(',')
            ?.map { it.trim().removeSuffix(".*").removeSuffix("*").trimEnd('.') }
            ?.filter { it.isNotEmpty() }
            ?: listOf("hu.bme.aut.resource_server")

        val annotationFqcn = "hu.bme.aut.resource_server.utils.BusinessCritical"

        val xmlFile = File("build/reports/jacoco/test/jacocoTestReport.xml")
        val jacocoMap = parseJacocoXml(xmlFile)

        var totalCovered = 0L
        var totalMissed = 0L

        packagesToScan.forEach { pkg ->
            val (c, m) = checkCoverageForPackage(pkg, jacocoMap, annotationFqcn)
            println("Package '$pkg' -> covered=$c, missed=$m")
            totalCovered += c
            totalMissed += m
        }

        val coverage = if (totalCovered + totalMissed == 0L) 1.0 else totalCovered.toDouble() / (totalCovered + totalMissed)
        println("BusinessCritical coverage: ${"%.2f".format(coverage * 100)}%")

        if (coverage < threshold) {
            fail("BusinessCritical coverage below threshold: ${"%.2f".format(coverage * 100)}% < ${threshold * 100}%")
        }
    }
}