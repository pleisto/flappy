"use strict";(self.webpackChunk_pleisto_flappy_docs=self.webpackChunk_pleisto_flappy_docs||[]).push([[932],{2599:(e,n,t)=>{t.d(n,{Z:()=>s});t(7378);var i=t(7140);const a={tabItem:"tabItem_wHwb"};var r=t(4246);function s(e){let{children:n,hidden:t,className:s}=e;return(0,r.jsx)("div",{role:"tabpanel",className:(0,i.Z)(a.tabItem,s),hidden:t,children:n})}},8447:(e,n,t)=>{t.d(n,{Z:()=>j});var i=t(7378),a=t(7140),r=t(9169),s=t(3620),l=t(9749),o=t(8981),u=t(56),c=t(8796);function d(e){return i.Children.toArray(e).filter((e=>"\n"!==e)).map((e=>{if(!e||(0,i.isValidElement)(e)&&function(e){const{props:n}=e;return!!n&&"object"==typeof n&&"value"in n}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}function p(e){const{values:n,children:t}=e;return(0,i.useMemo)((()=>{const e=n??function(e){return d(e).map((e=>{let{props:{value:n,label:t,attributes:i,default:a}}=e;return{value:n,label:t,attributes:i,default:a}}))}(t);return function(e){const n=(0,u.l)(e,((e,n)=>e.value===n.value));if(n.length>0)throw new Error(`Docusaurus error: Duplicate values "${n.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[n,t])}function h(e){let{value:n,tabValues:t}=e;return t.some((e=>e.value===n))}function f(e){let{queryString:n=!1,groupId:t}=e;const a=(0,s.k6)(),r=function(e){let{queryString:n=!1,groupId:t}=e;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return t??null}({queryString:n,groupId:t});return[(0,o._X)(r),(0,i.useCallback)((e=>{if(!r)return;const n=new URLSearchParams(a.location.search);n.set(r,e),a.replace({...a.location,search:n.toString()})}),[r,a])]}function g(e){const{defaultValue:n,queryString:t=!1,groupId:a}=e,r=p(e),[s,o]=(0,i.useState)((()=>function(e){let{defaultValue:n,tabValues:t}=e;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!h({value:n,tabValues:t}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${n}" but none of its children has the corresponding value. Available values are: ${t.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return n}const i=t.find((e=>e.default))??t[0];if(!i)throw new Error("Unexpected error: 0 tabValues");return i.value}({defaultValue:n,tabValues:r}))),[u,d]=f({queryString:t,groupId:a}),[g,m]=function(e){let{groupId:n}=e;const t=function(e){return e?`docusaurus.tab.${e}`:null}(n),[a,r]=(0,c.Nk)(t);return[a,(0,i.useCallback)((e=>{t&&r.set(e)}),[t,r])]}({groupId:a}),y=(()=>{const e=u??g;return h({value:e,tabValues:r})?e:null})();(0,l.Z)((()=>{y&&o(y)}),[y]);return{selectedValue:s,selectValue:(0,i.useCallback)((e=>{if(!h({value:e,tabValues:r}))throw new Error(`Can't select invalid tab value=${e}`);o(e),d(e),m(e)}),[d,m,r]),tabValues:r}}var m=t(362);const y={tabList:"tabList_J5MA",tabItem:"tabItem_l0OV"};var b=t(4246);function v(e){let{className:n,block:t,selectedValue:i,selectValue:s,tabValues:l}=e;const o=[],{blockElementScrollPositionUntilNextRender:u}=(0,r.o5)(),c=e=>{const n=e.currentTarget,t=o.indexOf(n),a=l[t].value;a!==i&&(u(n),s(a))},d=e=>{let n=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":{const t=o.indexOf(e.currentTarget)+1;n=o[t]??o[0];break}case"ArrowLeft":{const t=o.indexOf(e.currentTarget)-1;n=o[t]??o[o.length-1];break}}n?.focus()};return(0,b.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,a.Z)("tabs",{"tabs--block":t},n),children:l.map((e=>{let{value:n,label:t,attributes:r}=e;return(0,b.jsx)("li",{role:"tab",tabIndex:i===n?0:-1,"aria-selected":i===n,ref:e=>o.push(e),onKeyDown:d,onClick:c,...r,className:(0,a.Z)("tabs__item",y.tabItem,r?.className,{"tabs__item--active":i===n}),children:t??n},n)}))})}function x(e){let{lazy:n,children:t,selectedValue:a}=e;const r=(Array.isArray(t)?t:[t]).filter(Boolean);if(n){const e=r.find((e=>e.props.value===a));return e?(0,i.cloneElement)(e,{className:"margin-top--md"}):null}return(0,b.jsx)("div",{className:"margin-top--md",children:r.map(((e,n)=>(0,i.cloneElement)(e,{key:n,hidden:e.props.value!==a})))})}function w(e){const n=g(e);return(0,b.jsxs)("div",{className:(0,a.Z)("tabs-container",y.tabList),children:[(0,b.jsx)(v,{...e,...n}),(0,b.jsx)(x,{...e,...n})]})}function j(e){const n=(0,m.Z)();return(0,b.jsx)(w,{...e,children:d(e.children)},String(n))}},1904:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>o,default:()=>h,frontMatter:()=>l,metadata:()=>u,toc:()=>d});var i=t(4246),a=t(1670),r=t(8447),s=t(2599);const l={sidebar_position:3},o="Synthesized Function",u={id:"sythesized-function",title:"Synthesized Function",description:"A SynthesizedFunction is a key functionality offered within the Flappy. This powerful feature interacts with Large Language Models (LLMs) and requires only the definition of its description, inputs, and outputs, significantly simplifying the process of AI integration in your projects.",source:"@site/docs/sythesized-function.mdx",sourceDirName:".",slug:"/sythesized-function",permalink:"/ja/docs/sythesized-function",draft:!1,unlisted:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/sythesized-function.mdx",tags:[],version:"current",sidebarPosition:3,frontMatter:{sidebar_position:3},sidebar:"docSidebar",previous:{title:"\u306f\u3058\u3081\u306b",permalink:"/ja/docs/quick-start"},next:{title:"Invoke Function",permalink:"/ja/docs/invoke-function"}},c={},d=[{value:"Usage",id:"usage",level:2},{value:"Benefits",id:"benefits",level:2},{value:"Error Handling",id:"error-handling",level:2},{value:"Credits",id:"credits",level:2}];function p(e){const n={a:"a",code:"code",h1:"h1",h2:"h2",li:"li",ol:"ol",p:"p",pre:"pre",...(0,a.a)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.h1,{id:"synthesized-function",children:"Synthesized Function"}),"\n",(0,i.jsx)(n.p,{children:"A SynthesizedFunction is a key functionality offered within the Flappy. This powerful feature interacts with Large Language Models (LLMs) and requires only the definition of its description, inputs, and outputs, significantly simplifying the process of AI integration in your projects."}),"\n",(0,i.jsx)(n.p,{children:"SynthesizedFunction is designed to delegate the execution logic to the underlying LLM, such as GPT-3.5 Turbo. This approach allows developers to describe what they want the function to achieve, leaving the execution details to the LLM."}),"\n",(0,i.jsx)(n.h2,{id:"usage",children:"Usage"}),"\n",(0,i.jsx)(n.p,{children:"Creating a SynthesizedFunction involves specifying a description, along with the structures of the inputs and outputs. These definitions can be made using your preferred programming language, enhancing ease-of-use and accessibility. Flappy then parses these definitions using Abstract Syntax Tree (AST) parsing to transform them into a JSON Schema schema for machine readability and interoperability."}),"\n",(0,i.jsx)(n.p,{children:"Here's a typical example of a SynthesizedFunction:"}),"\n",(0,i.jsxs)(r.Z,{children:[(0,i.jsx)(s.Z,{value:"nodejs",label:"NodeJS (TypeScript)",default:!0,children:(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-ts",children:"import { createSynthesizedFunction, z } from '@pleisto/node-flappy'\n\ncreateSynthesizedFunction({\n  name: 'getMeta',\n  description: 'Extract meta data from a lawsuit full text.',\n  args: z.object({\n    lawsuit: z.string().describe('Lawsuit full text.')\n  }),\n  returnType: z.object({\n    verdict: z.nativeEnum(Verdict),\n    plaintiff: z.string(),\n    defendant: z.string(),\n    judgeOptions: z.array(z.string())\n  })\n})\n"})})}),(0,i.jsx)(s.Z,{value:"java",label:"Java",default:!0,children:(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-java",children:'  public static FlappyFunctionBase<?, ?> lawGetMeta = new FlappySynthesizedFunction(\n    "getMeta",\n    "Extract meta data from a lawsuit full text.",\n    LawMetaArguments.class,\n    LawMetaReturn.class\n  );\n\n  static class LawMetaArguments {\n    @FlappyField(description = "Lawsuit full text.")\n    String lawsuit;\n\n    public String getLawsuit() {\n      return lawsuit;\n    }\n\n    public void setLawsuit(String lawsuit) {\n      this.lawsuit = lawsuit;\n    }\n  }\n\n  public static class LawMetaReturn {\n    @FlappyField\n    Verdict verdict;\n\n    @FlappyField\n    String plaintiff;\n\n    @FlappyField\n    String defendant;\n\n    @FlappyField\n    List<String> judgeOptions;\n\n    public Verdict getVerdict() {\n      return verdict;\n    }\n\n    public void setVerdict(Verdict verdict) {\n      this.verdict = verdict;\n    }\n\n    public String getPlaintiff() {\n      return plaintiff;\n    }\n\n    public void setPlaintiff(String plaintiff) {\n      this.plaintiff = plaintiff;\n    }\n\n    public String getDefendant() {\n      return defendant;\n    }\n\n    public void setDefendant(String defendant) {\n      this.defendant = defendant;\n    }\n\n    public List<String> getJudgeOptions() {\n      return judgeOptions;\n    }\n\n    public void setJudgeOptions(List<String> judgeOptions) {\n      this.judgeOptions = judgeOptions;\n    }\n  }\n'})})}),(0,i.jsx)(s.Z,{value:"kotlin",label:"Kotlin",default:!0,children:(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'val lawGetMeta = FlappySynthesizedFunction(\n  name = "getMeta",\n  description = "Extract meta data from a lawsuit full text.",\n  args = LawMetaArguments::class.java,\n  returnType = LawMetaReturn::class.java,\n)\n\nclass LawMetaArguments(\n  @FlappyField(description = "Lawsuit full text.")\n  val lawsuit: String\n)\n\nclass LawMetaReturn(\n  @FlappyField\n  val verdict: Verdict,\n\n  @FlappyField\n  val plaintiff: String,\n\n  @FlappyField\n  val defendant: String,\n\n  @FlappyField\n  val judgeOptions: List<String>\n)\n'})})}),(0,i.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,i.jsx)(n.p,{children:"Coming soon"})})]}),"\n",(0,i.jsx)(n.p,{children:"In this scenario, the getMeta function is aimed at extracting metadata from a lawsuit text. The function takes a lawsuit text string as input and returns an object containing the verdict, plaintiff, defendant, and judge options."}),"\n",(0,i.jsx)(n.h2,{id:"benefits",children:"Benefits"}),"\n",(0,i.jsx)(n.p,{children:"The SynthesizedFunction presents several key advantages:"}),"\n",(0,i.jsxs)(n.ol,{children:["\n",(0,i.jsx)(n.li,{children:"Simplicity: Developers need only define the function's purpose, inputs, and outputs, freeing them from the complexities of implementation details."}),"\n",(0,i.jsx)(n.li,{children:"Flexibility: The feature allows for defining intricate functions without explicitly programming the logic, lending to robust design possibilities."}),"\n",(0,i.jsx)(n.li,{children:"Efficiency: SynthesizedFunction capitalizes on AI's power to handle complex data processing tasks, enhancing productivity."}),"\n"]}),"\n",(0,i.jsx)(n.p,{children:"By harnessing SynthesizedFunction in the Flappy SDK, developers can more effectively incorporate AI into their applications, achieving new levels of reliability and efficiency."}),"\n",(0,i.jsx)(n.h2,{id:"error-handling",children:"Error Handling"}),"\n",(0,i.jsx)(n.p,{children:"The SynthesizedFunction feature is designed to be robust and reliable. Flappy will validate the LLM's response and try to repair it if it is invalid. If the response is still invalid, Flappy will throw an error."}),"\n",(0,i.jsx)(n.h2,{id:"credits",children:"Credits"}),"\n",(0,i.jsxs)(n.p,{children:["This feature is inspired by the Microsoft's ",(0,i.jsx)(n.a,{href:"https://github.com/microsoft/TypeChat",children:"TypeChat"})," project."]})]})}function h(e={}){const{wrapper:n}={...(0,a.a)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(p,{...e})}):p(e)}},1670:(e,n,t)=>{t.d(n,{Z:()=>l,a:()=>s});var i=t(7378);const a={},r=i.createContext(a);function s(e){const n=i.useContext(r);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:s(e.components),i.createElement(r.Provider,{value:n},e.children)}}}]);