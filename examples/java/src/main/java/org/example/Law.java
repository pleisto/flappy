package org.example;


import io.github.cdimascio.dotenv.Dotenv;
import flappy.*;
import flappy.annotations.FlappyField;
import flappy.llms.ChatGPT;
import flappy.llms.ChatGPTConfig;

import java.util.List;

public class Law {
    static final String MOCK_LAWSUIT_DATA =
            "As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad — his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. “It is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,” said Christopher Mattei, a Connecticut lawyer for the families. “The families are increasingly concerned and will continue to contest these matters in court.” In an Aug. 29 court filing, lawyers for the families said that if Jones doesn’t reduce his personal expenses to a “reasonable” level, they will ask the bankruptcy judge to bar him from “further waste of estate assets,” appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he’s not doing anything wrong.";

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        ChatGPT llm = new ChatGPT("gpt-3.5-turbo", new ChatGPTConfig(dotenv.get("OPENAI_TOKEN"), dotenv.get("OPENAI_API_BASE")));

        FlappyFunction<?, ?> lawGetMeta = new FlappySynthesizedFunction(
                "getMeta",
                "Extract meta data from a lawsuit full text.",
                LawMetaArguments.class,
                LawMetaReturn.class
        );


        FlappyFunction<?, ?> lawGetLatestLawsuitsByPlaintiff = new FlappyInvokeFunction(
                "getLatestLawsuitsByPlaintiff",
                "Get the latest lawsuits by plaintiff.",
                GetLatestLawsuitsArguments.class,
                GetLatestLawsuitsReturn.class,
                (a, agent, $completion) -> new GetLatestLawsuitsReturn(MOCK_LAWSUIT_DATA)

        );

        FlappyBaseAgent lawAgent = new FlappyBaseAgent(
                llm, List.of(lawGetMeta, lawGetLatestLawsuitsByPlaintiff)
        );


        lawAgent.executePlan("Find the latest case with the plaintiff being families of victims and return its metadata.", CoroutineCallback.Companion.call((output, error) -> {
            System.out.println("ok");
            System.out.println(output);
            System.out.println(error);
            System.out.println("####");
        }));
    }

    enum Verdict {
        Innocent, Guilty, Unknown
    }

    static class GetLatestLawsuitsReturn {
        @FlappyField
        String output;

        public GetLatestLawsuitsReturn(String output) {
            this.output = output;
        }

    }

    class LawMetaArguments {
        @FlappyField(description = "Lawsuit full text.")
        String lawsuit;
    }

    class LawMetaReturn {
        @FlappyField
        Verdict verdict;

        @FlappyField
        String plaintiff;

        @FlappyField
        String defendant;

        @FlappyField(subType = FieldType.STRING)
        List<String> judgeOptions;
    }

    class GetLatestLawsuitsArguments {
        @FlappyField
        String plaintiff;

        @FlappyField(description = "For demo purpose. set to False")
        Boolean arg1;

        @FlappyField(description = "ignore it", subType = FieldType.STRING, optional = true)
        List<String> arg2 = null;
    }

}