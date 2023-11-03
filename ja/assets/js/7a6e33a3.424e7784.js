"use strict";(self.webpackChunk_pleisto_flappy_docs=self.webpackChunk_pleisto_flappy_docs||[]).push([[932],{2599:(e,t,n)=>{n.d(t,{Z:()=>s});n(7378);var i=n(7140);const a={tabItem:"tabItem_wHwb"};var r=n(4246);function s(e){let{children:t,hidden:n,className:s}=e;return(0,r.jsx)("div",{role:"tabpanel",className:(0,i.Z)(a.tabItem,s),hidden:n,children:t})}},8447:(e,t,n)=>{n.d(t,{Z:()=>x});var i=n(7378),a=n(7140),r=n(9169),s=n(3620),l=n(9749),o=n(8981),u=n(56),c=n(8796);function d(e){return i.Children.toArray(e).filter((e=>"\n"!==e)).map((e=>{if(!e||(0,i.isValidElement)(e)&&function(e){const{props:t}=e;return!!t&&"object"==typeof t&&"value"in t}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}function p(e){const{values:t,children:n}=e;return(0,i.useMemo)((()=>{const e=t??function(e){return d(e).map((e=>{let{props:{value:t,label:n,attributes:i,default:a}}=e;return{value:t,label:n,attributes:i,default:a}}))}(n);return function(e){const t=(0,u.l)(e,((e,t)=>e.value===t.value));if(t.length>0)throw new Error(`Docusaurus error: Duplicate values "${t.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[t,n])}function f(e){let{value:t,tabValues:n}=e;return n.some((e=>e.value===t))}function h(e){let{queryString:t=!1,groupId:n}=e;const a=(0,s.k6)(),r=function(e){let{queryString:t=!1,groupId:n}=e;if("string"==typeof t)return t;if(!1===t)return null;if(!0===t&&!n)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return n??null}({queryString:t,groupId:n});return[(0,o._X)(r),(0,i.useCallback)((e=>{if(!r)return;const t=new URLSearchParams(a.location.search);t.set(r,e),a.replace({...a.location,search:t.toString()})}),[r,a])]}function g(e){const{defaultValue:t,queryString:n=!1,groupId:a}=e,r=p(e),[s,o]=(0,i.useState)((()=>function(e){let{defaultValue:t,tabValues:n}=e;if(0===n.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(t){if(!f({value:t,tabValues:n}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${t}" but none of its children has the corresponding value. Available values are: ${n.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return t}const i=n.find((e=>e.default))??n[0];if(!i)throw new Error("Unexpected error: 0 tabValues");return i.value}({defaultValue:t,tabValues:r}))),[u,d]=h({queryString:n,groupId:a}),[g,b]=function(e){let{groupId:t}=e;const n=function(e){return e?`docusaurus.tab.${e}`:null}(t),[a,r]=(0,c.Nk)(n);return[a,(0,i.useCallback)((e=>{n&&r.set(e)}),[n,r])]}({groupId:a}),y=(()=>{const e=u??g;return f({value:e,tabValues:r})?e:null})();(0,l.Z)((()=>{y&&o(y)}),[y]);return{selectedValue:s,selectValue:(0,i.useCallback)((e=>{if(!f({value:e,tabValues:r}))throw new Error(`Can't select invalid tab value=${e}`);o(e),d(e),b(e)}),[d,b,r]),tabValues:r}}var b=n(362);const y={tabList:"tabList_J5MA",tabItem:"tabItem_l0OV"};var m=n(4246);function v(e){let{className:t,block:n,selectedValue:i,selectValue:s,tabValues:l}=e;const o=[],{blockElementScrollPositionUntilNextRender:u}=(0,r.o5)(),c=e=>{const t=e.currentTarget,n=o.indexOf(t),a=l[n].value;a!==i&&(u(t),s(a))},d=e=>{let t=null;switch(e.key){case"Enter":c(e);break;case"ArrowRight":{const n=o.indexOf(e.currentTarget)+1;t=o[n]??o[0];break}case"ArrowLeft":{const n=o.indexOf(e.currentTarget)-1;t=o[n]??o[o.length-1];break}}t?.focus()};return(0,m.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,a.Z)("tabs",{"tabs--block":n},t),children:l.map((e=>{let{value:t,label:n,attributes:r}=e;return(0,m.jsx)("li",{role:"tab",tabIndex:i===t?0:-1,"aria-selected":i===t,ref:e=>o.push(e),onKeyDown:d,onClick:c,...r,className:(0,a.Z)("tabs__item",y.tabItem,r?.className,{"tabs__item--active":i===t}),children:n??t},t)}))})}function j(e){let{lazy:t,children:n,selectedValue:a}=e;const r=(Array.isArray(n)?n:[n]).filter(Boolean);if(t){const e=r.find((e=>e.props.value===a));return e?(0,i.cloneElement)(e,{className:"margin-top--md"}):null}return(0,m.jsx)("div",{className:"margin-top--md",children:r.map(((e,t)=>(0,i.cloneElement)(e,{key:t,hidden:e.props.value!==a})))})}function w(e){const t=g(e);return(0,m.jsxs)("div",{className:(0,a.Z)("tabs-container",y.tabList),children:[(0,m.jsx)(v,{...e,...t}),(0,m.jsx)(j,{...e,...t})]})}function x(e){const t=(0,b.Z)();return(0,m.jsx)(w,{...e,children:d(e.children)},String(t))}},1904:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>c,contentTitle:()=>o,default:()=>f,frontMatter:()=>l,metadata:()=>u,toc:()=>d});var i=n(4246),a=n(5318),r=n(8447),s=n(2599);const l={sidebar_position:3},o="Synthesized Function",u={id:"sythesized-function",title:"Synthesized Function",description:"A SynthesizedFunction is a key functionality offered within the Flappy. This powerful feature interacts with Large Language Models (LLMs) and requires only the definition of its description, inputs, and outputs, significantly simplifying the process of AI integration in your projects.",source:"@site/docs/sythesized-function.mdx",sourceDirName:".",slug:"/sythesized-function",permalink:"/ja/docs/sythesized-function",draft:!1,unlisted:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/sythesized-function.mdx",tags:[],version:"current",sidebarPosition:3,frontMatter:{sidebar_position:3},sidebar:"docSidebar",previous:{title:"\u306f\u3058\u3081\u306b",permalink:"/ja/docs/quick-start"},next:{title:"Invoke Function",permalink:"/ja/docs/invoke-function"}},c={},d=[{value:"Usage",id:"usage",level:2},{value:"Benefits",id:"benefits",level:2},{value:"Error Handling",id:"error-handling",level:2},{value:"Credits",id:"credits",level:2}];function p(e){const t={a:"a",code:"code",h1:"h1",h2:"h2",li:"li",ol:"ol",p:"p",pre:"pre",...(0,a.ah)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(t.h1,{id:"synthesized-function",children:"Synthesized Function"}),"\n",(0,i.jsx)(t.p,{children:"A SynthesizedFunction is a key functionality offered within the Flappy. This powerful feature interacts with Large Language Models (LLMs) and requires only the definition of its description, inputs, and outputs, significantly simplifying the process of AI integration in your projects."}),"\n",(0,i.jsx)(t.p,{children:"SynthesizedFunction is designed to delegate the execution logic to the underlying LLM, such as GPT-3.5 Turbo. This approach allows developers to describe what they want the function to achieve, leaving the execution details to the LLM."}),"\n",(0,i.jsx)(t.h2,{id:"usage",children:"Usage"}),"\n",(0,i.jsx)(t.p,{children:"Creating a SynthesizedFunction involves specifying a description, along with the structures of the inputs and outputs. These definitions can be made using your preferred programming language, enhancing ease-of-use and accessibility. Flappy then parses these definitions using Abstract Syntax Tree (AST) parsing to transform them into a JSON Schema schema for machine readability and interoperability."}),"\n",(0,i.jsx)(t.p,{children:"Here's a typical example of a SynthesizedFunction:"}),"\n",(0,i.jsxs)(r.Z,{children:[(0,i.jsx)(s.Z,{value:"nodejs",label:"NodeJS (TypeScript)",default:!0,children:(0,i.jsx)(t.pre,{children:(0,i.jsx)(t.code,{className:"language-ts",children:"import { createSynthesizedFunction, z } from '@pleisto/node-flappy'\n\ncreateSynthesizedFunction({\n  name: 'getMeta',\n  description: 'Extract meta data from a lawsuit full text.',\n  args: z.object({\n    lawsuit: z.string().describe('Lawsuit full text.')\n  }),\n  returnType: z.object({\n    verdict: z.nativeEnum(Verdict),\n    plaintiff: z.string(),\n    defendant: z.string(),\n    judgeOptions: z.array(z.string())\n  })\n})\n"})})}),(0,i.jsx)(s.Z,{value:"java",label:"Java",default:!0,children:(0,i.jsx)(t.pre,{children:(0,i.jsx)(t.code,{className:"language-java",children:'  public static FlappyFunctionBase<?, ?> lawGetMeta = new FlappySynthesizedFunction(\n    "getMeta",\n    "Extract meta data from a lawsuit full text.",\n    LawMetaArguments.class,\n    LawMetaReturn.class\n  );\n\n  static class LawMetaArguments {\n    @FlappyField(description = "Lawsuit full text.")\n    String lawsuit;\n\n    public String getLawsuit() {\n      return lawsuit;\n    }\n\n    public void setLawsuit(String lawsuit) {\n      this.lawsuit = lawsuit;\n    }\n  }\n\n  public static class LawMetaReturn {\n    @FlappyField\n    Verdict verdict;\n\n    @FlappyField\n    String plaintiff;\n\n    @FlappyField\n    String defendant;\n\n    @FlappyField\n    List<String> judgeOptions;\n\n    public Verdict getVerdict() {\n      return verdict;\n    }\n\n    public void setVerdict(Verdict verdict) {\n      this.verdict = verdict;\n    }\n\n    public String getPlaintiff() {\n      return plaintiff;\n    }\n\n    public void setPlaintiff(String plaintiff) {\n      this.plaintiff = plaintiff;\n    }\n\n    public String getDefendant() {\n      return defendant;\n    }\n\n    public void setDefendant(String defendant) {\n      this.defendant = defendant;\n    }\n\n    public List<String> getJudgeOptions() {\n      return judgeOptions;\n    }\n\n    public void setJudgeOptions(List<String> judgeOptions) {\n      this.judgeOptions = judgeOptions;\n    }\n  }\n'})})}),(0,i.jsx)(s.Z,{value:"kotlin",label:"Kotlin",default:!0,children:(0,i.jsx)(t.pre,{children:(0,i.jsx)(t.code,{className:"language-kotlin",children:'val lawGetMeta = FlappySynthesizedFunction(\n  name = "getMeta",\n  description = "Extract meta data from a lawsuit full text.",\n  args = LawMetaArguments::class.java,\n  returnType = LawMetaReturn::class.java,\n)\n\nclass LawMetaArguments(\n  @FlappyField(description = "Lawsuit full text.")\n  val lawsuit: String\n)\n\nclass LawMetaReturn(\n  @FlappyField\n  val verdict: Verdict,\n\n  @FlappyField\n  val plaintiff: String,\n\n  @FlappyField\n  val defendant: String,\n\n  @FlappyField\n  val judgeOptions: List<String>\n)\n'})})}),(0,i.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,i.jsx)(t.p,{children:"Coming soon"})})]}),"\n",(0,i.jsx)(t.p,{children:"In this scenario, the getMeta function is aimed at extracting metadata from a lawsuit text. The function takes a lawsuit text string as input and returns an object containing the verdict, plaintiff, defendant, and judge options."}),"\n",(0,i.jsx)(t.h2,{id:"benefits",children:"Benefits"}),"\n",(0,i.jsx)(t.p,{children:"The SynthesizedFunction presents several key advantages:"}),"\n",(0,i.jsxs)(t.ol,{children:["\n",(0,i.jsx)(t.li,{children:"Simplicity: Developers need only define the function's purpose, inputs, and outputs, freeing them from the complexities of implementation details."}),"\n",(0,i.jsx)(t.li,{children:"Flexibility: The feature allows for defining intricate functions without explicitly programming the logic, lending to robust design possibilities."}),"\n",(0,i.jsx)(t.li,{children:"Efficiency: SynthesizedFunction capitalizes on AI's power to handle complex data processing tasks, enhancing productivity."}),"\n"]}),"\n",(0,i.jsx)(t.p,{children:"By harnessing SynthesizedFunction in the Flappy SDK, developers can more effectively incorporate AI into their applications, achieving new levels of reliability and efficiency."}),"\n",(0,i.jsx)(t.h2,{id:"error-handling",children:"Error Handling"}),"\n",(0,i.jsx)(t.p,{children:"The SynthesizedFunction feature is designed to be robust and reliable. Flappy will validate the LLM's response and try to repair it if it is invalid. If the response is still invalid, Flappy will throw an error."}),"\n",(0,i.jsx)(t.h2,{id:"credits",children:"Credits"}),"\n",(0,i.jsxs)(t.p,{children:["This feature is inspired by the Microsoft's ",(0,i.jsx)(t.a,{href:"https://github.com/microsoft/TypeChat",children:"TypeChat"})," project."]})]})}function f(e={}){const{wrapper:t}={...(0,a.ah)(),...e.components};return t?(0,i.jsx)(t,{...e,children:(0,i.jsx)(p,{...e})}):p(e)}},5318:(e,t,n)=>{n.d(t,{ah:()=>u});var i=n(7378);function a(e,t,n){return t in e?Object.defineProperty(e,t,{value:n,enumerable:!0,configurable:!0,writable:!0}):e[t]=n,e}function r(e,t){var n=Object.keys(e);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);t&&(i=i.filter((function(t){return Object.getOwnPropertyDescriptor(e,t).enumerable}))),n.push.apply(n,i)}return n}function s(e){for(var t=1;t<arguments.length;t++){var n=null!=arguments[t]?arguments[t]:{};t%2?r(Object(n),!0).forEach((function(t){a(e,t,n[t])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(n)):r(Object(n)).forEach((function(t){Object.defineProperty(e,t,Object.getOwnPropertyDescriptor(n,t))}))}return e}function l(e,t){if(null==e)return{};var n,i,a=function(e,t){if(null==e)return{};var n,i,a={},r=Object.keys(e);for(i=0;i<r.length;i++)n=r[i],t.indexOf(n)>=0||(a[n]=e[n]);return a}(e,t);if(Object.getOwnPropertySymbols){var r=Object.getOwnPropertySymbols(e);for(i=0;i<r.length;i++)n=r[i],t.indexOf(n)>=0||Object.prototype.propertyIsEnumerable.call(e,n)&&(a[n]=e[n])}return a}var o=i.createContext({}),u=function(e){var t=i.useContext(o),n=t;return e&&(n="function"==typeof e?e(t):s(s({},t),e)),n},c={inlineCode:"code",wrapper:function(e){var t=e.children;return i.createElement(i.Fragment,{},t)}},d=i.forwardRef((function(e,t){var n=e.components,a=e.mdxType,r=e.originalType,o=e.parentName,d=l(e,["components","mdxType","originalType","parentName"]),p=u(n),f=a,h=p["".concat(o,".").concat(f)]||p[f]||c[f]||r;return n?i.createElement(h,s(s({ref:t},d),{},{components:n})):i.createElement(h,s({ref:t},d))}));d.displayName="MDXCreateElement"}}]);