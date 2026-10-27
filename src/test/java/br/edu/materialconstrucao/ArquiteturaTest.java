package br.edu.materialconstrucao;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.junit.*;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "br.edu.materialconstrucao",
    importOptions = com.tngtech.archunit.core.importer.ImportOption.DoNotIncludeTests.class)
class ArquiteturaTest {
  @ArchTest
  static final ArchRule controllersNaoAcessamRepositories =
      noClasses()
          .that()
          .resideInAPackage("..controller..")
          .should()
          .dependOnClassesThat()
          .resideInAnyPackage("..repository..", "jakarta.persistence..")
          .because("controllers devem acessar o banco pelos services");

  @ArchTest
  static final ArchRule modelNaoDependeDeController =
      noClasses()
          .that()
          .resideInAPackage("..model..")
          .should()
          .dependOnClassesThat()
          .resideInAPackage("..controller..");
}
