package io.cleanslice.platform.architecture;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "io.cleanslice.platform")
@SuppressWarnings("unused")
class ArchitectureRulesTest {

    @ArchTest
    static final ArchRule service_must_not_depend_on_controller_or_infrastructure =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..controller..", "..infrastructure..");

    @ArchTest
    static final ArchRule controller_must_not_depend_on_infrastructure_or_ports =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..infrastructure..", "..application.port..");

    @ArchTest
    static final ArchRule domain_must_not_depend_on_outer_layers =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat()
                    .resideInAnyPackage("..controller..", "..service..", "..infrastructure..");

    @ArchTest
    static final ArchRule persistence_ports_must_be_interfaces =
            classes()
                    .that().resideInAPackage("..application.port.out.persistence..")
                    .should().beInterfaces();

    @ArchTest
    static final ArchRule messaging_ports_must_be_interfaces =
            classes()
                    .that().resideInAnyPackage(
                            "..application.port.in.messaging..",
                            "..application.port.out.messaging..")
                    .should().beInterfaces();
}
