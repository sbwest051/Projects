package edu.brown.cs.student.main;

import edu.brown.cs.student.main.server.exceptions.DatasourceException;

public class MockPDFSource implements PDFSource {
  private String sourceId;
  public MockPDFSource(){
    this.sourceId = "default";
  }
  @Override
  public String addURL(String url) throws DatasourceException {
    this.sourceId = url;
    return this.sourceId;
  }

  @Override
  public String addFile(String filepath) throws DatasourceException {
    this.sourceId = filepath;
    return this.sourceId;
  }

  @Override
  public String getContent(String sourceId, String question) throws DatasourceException {
    switch (sourceId){
      case "data/LargerTest/Asthma.pdf" -> {
        if (question.equals("Who were the subjects of the study?")){
          return "The study included 127 children with asthma who were diagnosed and treated at the"
              + " Private Practice Cebe-lica in Maribor, Slovenia, over a period of 12 consecutive months.";
        }
        return "This paper was a longitudinal study.";
      }
      case "data/LargerTest/Biopsy.pdf" -> {
        if (question.equals("Who were the subjects of the study?")){
          return "The study included 121 patients with peripheral lung infectious lesions.";
        }
        return "This paper was a prospective randomized study, which falls under the category of a longitudinal study.";
      }
      case "data/LargerTest/Ecoli.pdf" -> {
        if (question.equals("Who were the subjects of the study?")) {
          return "The subjects of the study were mice, which were intranasally immunized with "
              + "pVAXefa-1' and then challenged with E. coli strain EDL933 to examine the immune response "
              + "and protective effects of the DNA vaccine.";
        }
        return "This paper was an original research article, not a review paper, longitudinal study, or cross-sectional/cohort study.";
      }
      case "data/LargerTest/Iceland.pdf" -> {
        if (question.equals("Who were the subjects of the study?")) {
          return "The subjects of the study were adults in Iceland, specifically those within a "
              + "100-km driving distance from the Landspitali University Hospital. The study was "
              + "divided into three different periods for the analyses: 3 years prior to vaccination "
              + "(2009 to 2011, PreVac), 1 to 3 years postvaccination (2012 to 2014, PostVac-I), and 4 "
              + "to 6 years postvaccination (2015 to 2017, PostVac-II).";
        }
        return "This paper was a longitudinal study.";
      }
      case "data/LargerTest/Irrelevant.pdf" -> {
        if (question.equals("Who were the subjects of the study?")) {
          return "I'm sorry, I couldn't find relevant information about the specific subjects of "
              + "the study in the provided pages. However, the study appears to have involved pediatric "
              + "patients with atopic dermatitis.";
        }
        return "The type of study is not specified in the given pages.";
      }
      case "data/LargerTest/LLDD.pdf" -> {
        if (question.equals("Who were the subjects of the study?")) {
          return "The study involved a four-generation family with Alveolar capillary dysplasia "
              + "with misalignment of pulmonary veins (ACDMPV). Blood samples were collected from "
              + "the deceased neonate proband (IV:1), her parents (III:1 and III:2), grandparents "
              + "(II:1 and II:2), and the unaffected aunt (III:4), and the formalin-fixed "
              + "paraffin-embedded lung tissue obtained at autopsy from the proband (IV:1) and "
              + "her deceased aunt (III:3).";
        }
        return "This paper appears to be a cross-sectional/cohort study based on the information "
            + "provided. It presents findings from a specific case study of a four-generation "
            + "family with Alveolar capillary dysplasia with misalignment of pulmonary veins (ACDMPV).";
      }
      case "data/LargerTest/Sheep.pdf" -> {
        if (question.equals("Who were the subjects of the study?")) {
          return "The subjects of the study were six 20-month-old Suffolk cross sheep, consisting "
              + "of 5 females and 1 castrated male.";
        }
        return "This paper describes a longitudinal study in which lung brushing samples were taken "
            + "from three spatially disparate lung locations in six sheep at three time points "
            + "(baseline, 1 month, and 3 months).";
      }
      case "data/LargerTest/Strep.pdf" -> {
        if (question.equals("Who were the subjects of the study?")) {
          return "The study focused on Streptococcus pneumoniae serotype 1 clinical strains of "
              + "Indigenous Australian origin, including non-invasive and invasive isolates.";
        }
        return "This paper appears to be a research article that presents the findings of a "
            + "study on Streptococcus pneumoniae and its pathogenicity island.";
      }
      case "data/LargerTest/Vaccine.pdf" -> {
        if (question.equals("Who were the subjects of the study?")) {
          return "The subjects of the study were female cynomolgus macaques of Mauritius origin, "
              + "aged 8 to 10 years.";
        }
        return "This paper was a longitudinal study.";
      }
    }
    if (sourceId.equals("test")){
      switch (question) {
        case "foop" -> {
          return "yes";
        }
        case "yay" -> {
          return "no";
        }
      }
    }
    return "null";
  }

}
