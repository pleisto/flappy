package org.example.java;


import flappy.*;
import flappy.annotations.FlappyField;
import flappy.llms.ChatGPT;
import flappy.llms.ChatGPTConfig;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class Law {
  public static final String LAW_EXECUTE_PLAN_PROMPT = "Find the latest case with the plaintiff being families of victims and return its metadata.";
  static final String MOCK_LAWSUIT_DATA =
    "As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong.";
  public static FlappyFunction<?, ?> lawGetLatestLawsuitsByPlaintiff = new FlappyInvokeFunction(
    "getLatestLawsuitsByPlaintiff",
    "Get the latest lawsuits by plaintiff.",
    GetLatestLawsuitsArguments.class,
    String.class,
    (a, agent, $completion) -> MOCK_LAWSUIT_DATA
  );
  public static FlappyFunction<?, ?> lawGetMeta = new FlappySynthesizedFunction(
    "getMeta",
    "Extract meta data from a lawsuit full text.",
    LawMetaArguments.class,
    LawMetaReturn.class
  );

  public static void main(String[] args) throws ExecutionException, InterruptedException {
    Dotenv dotenv = Dotenv.load();
    ChatGPT llm = new ChatGPT("gpt-3.5-turbo", new ChatGPTConfig(dotenv.get("OPENAI_TOKEN"), dotenv.get("OPENAI_API_BASE")));


    FlappyBaseAgent lawAgent = new FlappyBaseAgent(
      llm, List.of(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
    );


    Future<LawMetaReturn> future = lawAgent.executePlanAsync(LAW_EXECUTE_PLAN_PROMPT);
    LawMetaReturn ret = future.get();

    System.out.println("################# RESULT ################");
    System.out.println(ret.getDefendant());
    System.out.println("################# RESULT ################");
  }

  public enum Verdict {
    Innocent, Guilty, Unknown
  }

  public static class LawMetaReturn {
    @FlappyField
    Verdict verdict;

    @FlappyField
    String plaintiff;

    @FlappyField
    String defendant;

    @FlappyField(subType = FieldType.STRING)
    List<String> judgeOptions;

    public Verdict getVerdict() {
      return verdict;
    }

    public void setVerdict(Verdict verdict) {
      this.verdict = verdict;
    }

    public String getPlaintiff() {
      return plaintiff;
    }

    public void setPlaintiff(String plaintiff) {
      this.plaintiff = plaintiff;
    }

    public String getDefendant() {
      return defendant;
    }

    public void setDefendant(String defendant) {
      this.defendant = defendant;
    }

    public List<String> getJudgeOptions() {
      return judgeOptions;
    }

    public void setJudgeOptions(List<String> judgeOptions) {
      this.judgeOptions = judgeOptions;
    }
  }

  static class GetLatestLawsuitsArguments {
    @FlappyField
    String plaintiff;

    @FlappyField(description = "For demo purpose. set to False")
    Boolean arg1;

    @FlappyField(description = "ignore it", subType = FieldType.STRING, optional = true)
    List<String> arg2 = null;

    public String getPlaintiff() {
      return plaintiff;
    }

    public void setPlaintiff(String plaintiff) {
      this.plaintiff = plaintiff;
    }

    public Boolean getArg1() {
      return arg1;
    }

    public void setArg1(Boolean arg1) {
      this.arg1 = arg1;
    }

    public List<String> getArg2() {
      return arg2;
    }

    public void setArg2(List<String> arg2) {
      this.arg2 = arg2;
    }
  }

  static class LawMetaArguments {
    @FlappyField(description = "Lawsuit full text.")
    String lawsuit;

    public String getLawsuit() {
      return lawsuit;
    }

    public void setLawsuit(String lawsuit) {
      this.lawsuit = lawsuit;
    }
  }

}
