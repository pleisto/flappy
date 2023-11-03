"use strict";(self.webpackChunk_pleisto_flappy_docs=self.webpackChunk_pleisto_flappy_docs||[]).push([[355],{2599:(e,n,t)=>{t.d(n,{Z:()=>s});t(7378);var a=t(7140);const r={tabItem:"tabItem_wHwb"};var l=t(4246);function s(e){let{children:n,hidden:t,className:s}=e;return(0,l.jsx)("div",{role:"tabpanel",className:(0,a.Z)(r.tabItem,s),hidden:t,children:n})}},8447:(e,n,t)=>{t.d(n,{Z:()=>w});var a=t(7378),r=t(7140),l=t(9169),s=t(3620),i=t(9749),o=t(8981),c=t(56),d=t(8796);function u(e){return a.Children.toArray(e).filter((e=>"\n"!==e)).map((e=>{if(!e||(0,a.isValidElement)(e)&&function(e){const{props:n}=e;return!!n&&"object"==typeof n&&"value"in n}(e))return e;throw new Error(`Docusaurus error: Bad <Tabs> child <${"string"==typeof e.type?e.type:e.type.name}>: all children of the <Tabs> component should be <TabItem>, and every <TabItem> should have a unique "value" prop.`)}))?.filter(Boolean)??[]}function h(e){const{values:n,children:t}=e;return(0,a.useMemo)((()=>{const e=n??function(e){return u(e).map((e=>{let{props:{value:n,label:t,attributes:a,default:r}}=e;return{value:n,label:t,attributes:a,default:r}}))}(t);return function(e){const n=(0,c.l)(e,((e,n)=>e.value===n.value));if(n.length>0)throw new Error(`Docusaurus error: Duplicate values "${n.map((e=>e.value)).join(", ")}" found in <Tabs>. Every value needs to be unique.`)}(e),e}),[n,t])}function p(e){let{value:n,tabValues:t}=e;return t.some((e=>e.value===n))}function f(e){let{queryString:n=!1,groupId:t}=e;const r=(0,s.k6)(),l=function(e){let{queryString:n=!1,groupId:t}=e;if("string"==typeof n)return n;if(!1===n)return null;if(!0===n&&!t)throw new Error('Docusaurus error: The <Tabs> component groupId prop is required if queryString=true, because this value is used as the search param name. You can also provide an explicit value such as queryString="my-search-param".');return t??null}({queryString:n,groupId:t});return[(0,o._X)(l),(0,a.useCallback)((e=>{if(!l)return;const n=new URLSearchParams(r.location.search);n.set(l,e),r.replace({...r.location,search:n.toString()})}),[l,r])]}function m(e){const{defaultValue:n,queryString:t=!1,groupId:r}=e,l=h(e),[s,o]=(0,a.useState)((()=>function(e){let{defaultValue:n,tabValues:t}=e;if(0===t.length)throw new Error("Docusaurus error: the <Tabs> component requires at least one <TabItem> children component");if(n){if(!p({value:n,tabValues:t}))throw new Error(`Docusaurus error: The <Tabs> has a defaultValue "${n}" but none of its children has the corresponding value. Available values are: ${t.map((e=>e.value)).join(", ")}. If you intend to show no default tab, use defaultValue={null} instead.`);return n}const a=t.find((e=>e.default))??t[0];if(!a)throw new Error("Unexpected error: 0 tabValues");return a.value}({defaultValue:n,tabValues:l}))),[c,u]=f({queryString:t,groupId:r}),[m,g]=function(e){let{groupId:n}=e;const t=function(e){return e?`docusaurus.tab.${e}`:null}(n),[r,l]=(0,d.Nk)(t);return[r,(0,a.useCallback)((e=>{t&&l.set(e)}),[t,l])]}({groupId:r}),b=(()=>{const e=c??m;return p({value:e,tabValues:l})?e:null})();(0,i.Z)((()=>{b&&o(b)}),[b]);return{selectedValue:s,selectValue:(0,a.useCallback)((e=>{if(!p({value:e,tabValues:l}))throw new Error(`Can't select invalid tab value=${e}`);o(e),u(e),g(e)}),[u,g,l]),tabValues:l}}var g=t(362);const b={tabList:"tabList_J5MA",tabItem:"tabItem_l0OV"};var j=t(4246);function v(e){let{className:n,block:t,selectedValue:a,selectValue:s,tabValues:i}=e;const o=[],{blockElementScrollPositionUntilNextRender:c}=(0,l.o5)(),d=e=>{const n=e.currentTarget,t=o.indexOf(n),r=i[t].value;r!==a&&(c(n),s(r))},u=e=>{let n=null;switch(e.key){case"Enter":d(e);break;case"ArrowRight":{const t=o.indexOf(e.currentTarget)+1;n=o[t]??o[0];break}case"ArrowLeft":{const t=o.indexOf(e.currentTarget)-1;n=o[t]??o[o.length-1];break}}n?.focus()};return(0,j.jsx)("ul",{role:"tablist","aria-orientation":"horizontal",className:(0,r.Z)("tabs",{"tabs--block":t},n),children:i.map((e=>{let{value:n,label:t,attributes:l}=e;return(0,j.jsx)("li",{role:"tab",tabIndex:a===n?0:-1,"aria-selected":a===n,ref:e=>o.push(e),onKeyDown:u,onClick:d,...l,className:(0,r.Z)("tabs__item",b.tabItem,l?.className,{"tabs__item--active":a===n}),children:t??n},n)}))})}function x(e){let{lazy:n,children:t,selectedValue:r}=e;const l=(Array.isArray(t)?t:[t]).filter(Boolean);if(n){const e=l.find((e=>e.props.value===r));return e?(0,a.cloneElement)(e,{className:"margin-top--md"}):null}return(0,j.jsx)("div",{className:"margin-top--md",children:l.map(((e,n)=>(0,a.cloneElement)(e,{key:n,hidden:e.props.value!==r})))})}function y(e){const n=m(e);return(0,j.jsxs)("div",{className:(0,r.Z)("tabs-container",b.tabList),children:[(0,j.jsx)(v,{...e,...n}),(0,j.jsx)(x,{...e,...n})]})}function w(e){const n=(0,g.Z)();return(0,j.jsx)(y,{...e,children:u(e.children)},String(n))}},2507:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>d,contentTitle:()=>o,default:()=>p,frontMatter:()=>i,metadata:()=>c,toc:()=>u});var a=t(4246),r=t(5318),l=t(8447),s=t(2599);const i={sidebar_position:2},o="\u5feb\u901f\u5f00\u59cb",c={id:"quick-start",title:"\u5feb\u901f\u5f00\u59cb",description:"\u26a0\ufe0f \u672c\u9879\u76ee\u4ecd\u5728\u5f00\u53d1\u4e2d\u3002\u6211\u4eec\u6b63\u52aa\u529b\u5c3d\u5feb\u53d1\u5e03Flappy\u7684\u9996\u4e2a\u7248\u672c\u3002\u656c\u8bf7\u671f\u5f85\uff01\u6587\u6863\u548c\u4ee3\u7801\u793a\u4f8b\u5c06\u5f88\u5feb\u63d0\u4f9b\u3002",source:"@site/i18n/zh-Hans/docusaurus-plugin-content-docs/current/quick-start.mdx",sourceDirName:".",slug:"/quick-start",permalink:"/zh-Hans/docs/quick-start",draft:!1,unlisted:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/quick-start.mdx",tags:[],version:"current",sidebarPosition:2,frontMatter:{sidebar_position:2},sidebar:"docSidebar",previous:{title:"\u7b80\u4ecb",permalink:"/zh-Hans/docs/"},next:{title:"Synthesized Function",permalink:"/zh-Hans/docs/sythesized-function"}},d={},u=[{value:"\u5b89\u88c5",id:"\u5b89\u88c5",level:2},{value:"\u521b\u5efaLLM\u9002\u914d\u5668",id:"\u521b\u5efallm\u9002\u914d\u5668",level:2},{value:"\u5b9a\u4e49\u4f60\u7684 Agent",id:"\u5b9a\u4e49\u4f60\u7684-agent",level:2},{value:"\u5173\u952e\u6982\u5ff5",id:"\u5173\u952e\u6982\u5ff5",level:3},{value:"\u51fd\u6570",id:"\u51fd\u6570",level:4},{value:"\u4ee3\u7801\u89e3\u91ca\u5668",id:"\u4ee3\u7801\u89e3\u91ca\u5668",level:4},{value:"\u8c03\u7528\u4f60\u7684\u4ee3\u7406",id:"\u8c03\u7528\u4f60\u7684\u4ee3\u7406",level:3},{value:"\u521b\u5efa\u5e76\u6267\u884c\u4e00\u4e2a\u884c\u52a8\u8ba1\u5212",id:"\u521b\u5efa\u5e76\u6267\u884c\u4e00\u4e2a\u884c\u52a8\u8ba1\u5212",level:3},{value:"\u76f4\u63a5\u8c03\u7528 synthesized function",id:"\u76f4\u63a5\u8c03\u7528-synthesized-function",level:3},{value:"\u8c03\u7528 Code Interpreter",id:"\u8c03\u7528-code-interpreter",level:3}];function h(e){const n={a:"a",admonition:"admonition",code:"code",h1:"h1",h2:"h2",h3:"h3",h4:"h4",li:"li",p:"p",pre:"pre",ul:"ul",...(0,r.ah)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(n.h1,{id:"\u5feb\u901f\u5f00\u59cb",children:"\u5feb\u901f\u5f00\u59cb"}),"\n",(0,a.jsx)(n.admonition,{type:"caution",children:(0,a.jsx)(n.p,{children:"\u26a0\ufe0f \u672c\u9879\u76ee\u4ecd\u5728\u5f00\u53d1\u4e2d\u3002\u6211\u4eec\u6b63\u52aa\u529b\u5c3d\u5feb\u53d1\u5e03Flappy\u7684\u9996\u4e2a\u7248\u672c\u3002\u656c\u8bf7\u671f\u5f85\uff01\u6587\u6863\u548c\u4ee3\u7801\u793a\u4f8b\u5c06\u5f88\u5feb\u63d0\u4f9b\u3002"})}),"\n",(0,a.jsx)(n.p,{children:"Flappy\u662f\u4e00\u4e2a\u7528\u4e8e\u6784\u5efa\u57fa\u4e8e\u5927\u8bed\u8a00\u6a21\u578b\uff08LLM\uff09\u7684AI Agent/AI\u5e94\u7528\u7a0b\u5e8f\u7684\u5f00\u53d1\u6846\u67b6\uff0c\u540c\u65f6\u652f\u6301\u591a\u79cd\u4e0d\u540c\u7f16\u7a0b\u8bed\u8a00\u3002\u8bf7\u6307\u5b9a\u60a8\u6b63\u5728\u4f7f\u7528\u7684\u8bed\u8a00\u4ee5\u5f00\u59cb\u4f7f\u7528\u3002"}),"\n",(0,a.jsx)(n.h2,{id:"\u5b89\u88c5",children:"\u5b89\u88c5"}),"\n",(0,a.jsxs)(l.Z,{children:[(0,a.jsx)(s.Z,{value:"nodejs",label:"NodeJS (TypeScript)",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-bash",children:"npm install @plesito/node-flappy@next\n# or yarn add @pleisto/node-flappy@next\n"})})}),(0,a.jsxs)(s.Z,{value:"java",label:"Java",default:!0,children:[(0,a.jsx)(n.p,{children:"Maven"}),(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-xml",children:"<dependency>\n    <groupId>com.pleisto</groupId>\n    <artifactId>flappy</artifactId>\n    <version>0.0.7</version>\n</dependency>\n"})}),(0,a.jsx)(n.p,{children:"Gradle"}),(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-sh",children:"implementation 'com.pleisto:flappy:0.0.7'\n"})})]}),(0,a.jsx)(s.Z,{value:"kotlin",label:"Kotlin",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'implementation("com.pleisto:flappy:0.0.7")\n'})})}),(0,a.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})})]}),"\n",(0,a.jsx)(n.h2,{id:"\u521b\u5efallm\u9002\u914d\u5668",children:"\u521b\u5efaLLM\u9002\u914d\u5668"}),"\n",(0,a.jsx)(n.p,{children:"Flappy\u652f\u6301\u591a\u79cdLLM\u5b9e\u73b0\uff0c\u4f8b\u5982OpenAI\u7684ChatGPT\u548chugingface\u7684transformers\u3002\u60a8\u4e5f\u53ef\u4ee5\u901a\u8fc7\u5b9e\u73b0LLMBase\u63a5\u53e3\u6765\u521b\u5efa\u81ea\u5df1\u7684LLM\u9002\u914d\u5668\u3002"}),"\n",(0,a.jsxs)(l.Z,{children:[(0,a.jsx)(s.Z,{value:"nodejs-chatgpt",label:"NodeJS (TypeScript) & ChatGPT",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-ts",children:"// you need to manually install `openai` package.\nimport { env } from 'node:process'\nimport OpenAI from 'openai'\nimport { ChatGPT } from '@pleisto/node-flappy'\n\nconst chatGpt = new ChatGPT(\n  new OpenAI({\n    apiKey: env.OPENAI_API_KEY!,\n    baseURL: env.OPENAI_API_BASE!\n  }),\n  'gpt-3.5-turbo'\n)\n"})})}),(0,a.jsx)(s.Z,{value:"nodejs-baichuan",label:"NodeJS (TypeScript) & \u767e\u5ddd53B\u5927\u6a21\u578bAPI",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-ts",children:"import { env } from 'node:process'\nimport { Baichuan } from '@pleisto/node-flappy'\n\nconst chatGpt = new Baichuan(\n {\n  baichuan_api_key: env.BAICHUAN_API_KEY!,\n  baichuan_secret_key: env.BAICHUAN_SECRET_KEY!,\n }\n)\n"})})}),(0,a.jsx)(s.Z,{value:"java",label:"Java  with ChatGPT",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-java",children:'    Dotenv dotenv = Dotenv.load();\n    ChatGPT llm = new ChatGPT("gpt-3.5-turbo", new ChatGPT.ChatGPTConfig(dotenv.get("OPENAI_TOKEN"), dotenv.get("OPENAI_API_BASE")));\n'})})}),(0,a.jsx)(s.Z,{value:"kotlin",label:"Kotlin  with ChatGPT",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:'  val dotenv = dotenv()\n\n  val chatGPT = ChatGPT(\n    model = "gpt-3.5-turbo",\n    chatGPTConfig = ChatGPT.ChatGPTConfig(token = dotenv["OPENAI_TOKEN"], host = dotenv["OPENAI_API_BASE"])\n  )\n'})})}),(0,a.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})})]}),"\n",(0,a.jsx)(n.h2,{id:"\u5b9a\u4e49\u4f60\u7684-agent",children:"\u5b9a\u4e49\u4f60\u7684 Agent"}),"\n",(0,a.jsx)(n.p,{children:"\u5728\u4eba\u5de5\u667a\u80fd\u4e2d\uff0cAgent \u662f\u4e00\u4e2a\u8ba1\u7b97\u673a\u7a0b\u5e8f\u6216\u7cfb\u7edf\uff0c\u5176\u8bbe\u8ba1\u76ee\u7684\u662f\u611f\u77e5\u5176\u73af\u5883\uff0c\u505a\u51fa\u51b3\u5b9a\uff0c\u5e76\u91c7\u53d6\u884c\u52a8\u4ee5\u5b9e\u73b0\u7279\u5b9a\u7684\u76ee\u6807\u6216\u4e00\u7ec4\u76ee\u6807\u3002Agent \u81ea\u4e3b\u8fd0\u884c\uff0c\u610f\u5473\u7740\u5b83\u4e0d\u76f4\u63a5\u53d7\u4eba\u7c7b\u64cd\u4f5c\u5458\u7684\u63a7\u5236\u3002"}),"\n",(0,a.jsx)(n.p,{children:"\u5728 Flappy \u7684\u751f\u6001\u7cfb\u7edf\u4e2d\uff0cAgent \u4f5c\u4e3a LLM \u7684\u591a\u529f\u80fd\u901a\u9053\u8fdb\u884c\u64cd\u4f5c\u3002\u5b83\u7684\u8bbe\u8ba1\u76ee\u7684\u662f\u8fdb\u884c\u5404\u79cd\u4efb\u52a1\u7684\u5904\u7406 - \u7ed3\u6784\u5316\u6570\u636e\uff0c\u8c03\u7528\u5916\u90e8 API\uff0c\u6216\u8005\u5bf9 LLM \u751f\u6210\u7684 Python \u4ee3\u7801\u8fdb\u884c\u6c99\u76d2\u5904\u7406 - \u6839\u636e\u9700\u8981\u4ee5\u4efb\u4f55\u7ec4\u5408\u8fdb\u884c\u3002\u5b83\u662f\u4e00\u4e2a\u7075\u6d3b\u7684\u5de5\u5177\uff0c\u800c\u4e0d\u662f\u4e00\u4e2a\u50f5\u5316\u7684\u9f7f\u8f6e\uff0c\u80fd\u591f\u9002\u5e94\u60a8\u5bf9\u66f4\u9ad8\u6548\u548c\u5b89\u5168\u7684 LLM \u4ea4\u4e92\u7684\u9700\u6c42\u3002"}),"\n",(0,a.jsx)(n.h3,{id:"\u5173\u952e\u6982\u5ff5",children:"\u5173\u952e\u6982\u5ff5"}),"\n",(0,a.jsx)(n.h4,{id:"\u51fd\u6570",children:"\u51fd\u6570"}),"\n",(0,a.jsxs)(n.p,{children:["\u51fd\u6570\u4f5c\u4e3a Flappy \u6846\u67b6\u4e2d\u4f60\u7684 Agent \u7684\u57fa\u7840\u3002\u5728\u8fd9\u4e2a\u6587\u6863\u4e2d\uff0c\u6211\u4eec\u5c06\u4ecb\u7ecd ",(0,a.jsx)(n.code,{children:"InvokeFunction"})," \u548c ",(0,a.jsx)(n.code,{children:"SythesizedFunction"}),"\uff0c\u8fd9\u4e24\u79cd\u4f60\u53ef\u4ee5\u5b9a\u4e49\u548c\u4f7f\u7528\u7684\u57fa\u7840\u51fd\u6570\u7c7b\u578b\u3002"]}),"\n",(0,a.jsxs)(n.ul,{children:["\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"InvokeFunction"})," \u4f7f Agent \u80fd\u591f\u4e0e\u73af\u5883\u8fdb\u884c\u4ea4\u4e92\uff0c\u7c7b\u4f3c\u4e8e Langchain \u7684 Agent \u4e2d\u7684\u5de5\u5177\u3002\u5b83\u7531\u8f93\u5165\u548c\u8f93\u51fa\u53c2\u6570\u5b9a\u4e49\uff0c\u8fd9\u4e9b\u53c2\u6570\u7684\u7ed3\u6784\u5fc5\u987b\u6e05\u6670\uff0c\u4ee5\u4fbf\u8bed\u8a00\u5b66\u4e60\u6a21\u578b\uff08LLM\uff09\u80fd\u591f\u6709\u6548\u5730\u8fdb\u884c\u4ea4\u4e92\u3002\u7406\u89e3\u8fd9\u4e9b\u53c2\u6570\u4ee5\u53ca\u51fd\u6570\u5728\u4e0e\u7528\u6237\u8bf7\u6c42\u548c\u4e16\u754c\u4ea4\u4e92\u4e2d\u7684\u89d2\u8272\u662f\u975e\u5e38\u91cd\u8981\u7684\uff0c\u5b83\u5bf9\u4e8e\u6210\u672c\u6548\u76ca\u7684\u4efb\u52a1\u8ba1\u5212\u81f3\u5173\u91cd\u8981\u3002"]}),"\n",(0,a.jsxs)(n.li,{children:[(0,a.jsx)(n.code,{children:"SythesizedFunction"})," \u662f\u7531 LLM \u626e\u6f14\u7684\u4e00\u79cd\u51fd\u6570\u3002\u4f60\u53ea\u9700\u8981\u5b9a\u4e49\u5b83\u7684\u63cf\u8ff0\u548c\u5b83\u7684\u8f93\u5165\u548c\u8f93\u51fa\u7684\u7ed3\u6784\u3002\u7136\u540e\uff0cLLM \u5c06\u5c1d\u8bd5\u6309\u7167\u5b9a\u4e49\u7684\u683c\u5f0f\u5904\u7406\u8f93\u5165\u5e76\u4ea7\u751f\u9884\u671f\u683c\u5f0f\u7684\u8f93\u51fa\u3002\u8fd9\u4f7f\u5f97 SythesizedFunction \u5728 LLM \u6267\u884c\u7ed3\u6784\u5316\u6570\u636e\u63d0\u53d6\u4efb\u52a1\uff0c\u6216\u8005\u9884\u671f LLM \u8f93\u51fa\u7ed3\u6784\u5316\u6570\u636e\u7684\u573a\u666f\u4e2d\u5c24\u5176\u6709\u7528\u3002"]}),"\n"]}),"\n",(0,a.jsx)(n.h4,{id:"\u4ee3\u7801\u89e3\u91ca\u5668",children:"\u4ee3\u7801\u89e3\u91ca\u5668"}),"\n",(0,a.jsx)(n.p,{children:"Flappy \u4e2d\u7684\u4ee3\u7801\u89e3\u91ca\u5668\u4f5c\u4e3a ChatGPT \u4ee3\u7801\u89e3\u91ca\u5668\u7684\u7b49\u6548\u66ff\u4ee3\u54c1\uff0c\u4f7f\u8bed\u8a00\u6a21\u578b\u80fd\u591f\u901a\u8fc7 Python \u4ee3\u7801\u6ee1\u8db3\u590d\u6742\u7684\u7528\u6237\u9700\u6c42\u3002Flappy \u7684\u4ee3\u7801\u89e3\u91ca\u5668\u4e0e\u5e02\u573a\u4e0a\u5176\u4ed6\u5f00\u6e90\u5b9e\u73b0\u7684\u533a\u522b\u5728\u4e8e\u5176\u6c99\u76d2\u5316\u7684\u5b89\u5168\u7279\u6027\u3002\u8fd9\u786e\u4fdd\u4e86\u5b83\u6ee1\u8db3\u751f\u4ea7\u73af\u5883\u90e8\u7f72\u6240\u5fc5\u9700\u7684\u4e25\u683c\u7684\u5b89\u5168\u9700\u6c42\u3002"}),"\n",(0,a.jsxs)(l.Z,{children:[(0,a.jsx)(s.Z,{value:"nodejs",label:"NodeJS (TypeScript)",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-ts",children:"import { createFlappyAgent,  createInvokeFunction,  createSynthesizedFunction, createCodeInterpreter,  z } from '@pleisto/node-flappy'\n\nenum Verdict {\n  Innocent = 'Innocent',\n  Guilty = 'Guilty',\n  Unknown = 'Unknown'\n}\n\nconst MOCK_LAWSUIT_DATA =\n  \"As Alex Jones continues telling his Infowars audience about his money problems and pleads for them to buy his products, his own documents show life is not all that bad \u2014 his net worth is around $14 million and his personal spending topped $93,000 in July alone, including thousands of dollars on meals and entertainment. The conspiracy theorist and his lawyers file monthly financial reports in his personal bankruptcy case, and the latest one has struck a nerve with the families of victims of Sandy Hook Elementary School shooting. They're still seeking the $1.5 billion they won last year in lawsuits against Jones and his media company for repeatedly calling the 2012 massacre a hoax on his shows. \u201cIt is disturbing that Alex Jones continues to spend money on excessive household expenditures and his extravagant lifestyle when that money rightfully belongs to the families he spent years tormenting,\u201d said Christopher Mattei, a Connecticut lawyer for the families. \u201cThe families are increasingly concerned and will continue to contest these matters in court.\u201d In an Aug. 29 court filing, lawyers for the families said that if Jones doesn\u2019t reduce his personal expenses to a \u201creasonable\u201d level, they will ask the bankruptcy judge to bar him from \u201cfurther waste of estate assets,\u201d appoint a trustee to oversee his spending, or dismiss the bankruptcy case. On his Infowars show Tuesday, Jones said he\u2019s not doing anything wrong.\"\n\nconst agent = createFlappyAgent({\n  llm: chatGpt,\n  // Define your agent's functions.\n  functions: [\n    createCodeInterpreter(),\n    createSynthesizedFunction({\n      name: 'getMeta',\n      description: 'Extract meta data from a lawsuit full text.',\n      args: z.object({\n        lawsuit: z.string().describe('Lawsuit full text.')\n      }),\n      returnType: z.object({\n        verdict: z.nativeEnum(Verdict),\n        plaintiff: z.string(),\n        defendant: z.string(),\n        judgeOptions: z.array(z.string())\n      })\n    }),\n    createInvokeFunction({\n      name: 'getLatestLawsuitsByPlaintiff',\n      description: 'Get the latest lawsuits by plaintiff.',\n      args: z.object({\n        plaintiff: z.string(),\n        arg1: z.boolean().describe('For demo purpose. set to False'),\n        arg2: z.array(z.string()).describe('ignore it').optional()\n      }),\n      returnType: z.string(),\n      resolve: async args => {\n        // Do something\n        // e.g. query SQL database\n        console.debug('getLatestLawsuitsByPlaintiff called', args)\n        return MOCK_LAWSUIT_DATA\n      }\n    })\n  ]\n})\n"})})}),(0,a.jsx)(s.Z,{value:"java",label:"Java",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})}),(0,a.jsx)(s.Z,{value:"kotlin",label:"Kotlin",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})}),(0,a.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})})]}),"\n",(0,a.jsx)(n.h3,{id:"\u8c03\u7528\u4f60\u7684\u4ee3\u7406",children:"\u8c03\u7528\u4f60\u7684\u4ee3\u7406"}),"\n",(0,a.jsx)(n.h3,{id:"\u521b\u5efa\u5e76\u6267\u884c\u4e00\u4e2a\u884c\u52a8\u8ba1\u5212",children:"\u521b\u5efa\u5e76\u6267\u884c\u4e00\u4e2a\u884c\u52a8\u8ba1\u5212"}),"\n",(0,a.jsx)(n.p,{children:"\u589e\u5f3a\u8bed\u8a00\u6a21\u578b\uff08ALMs\uff09\u5c06\u5927\u578b\u8bed\u8a00\u6a21\u578b\uff08LLMs\uff09\u7684\u63a8\u7406\u80fd\u529b\u4e0e\u5141\u8bb8\u77e5\u8bc6\u68c0\u7d22\u548c\u884c\u52a8\u6267\u884c\u7684\u5de5\u5177\u76f8\u7ed3\u5408\u3002\u73b0\u6709\u7684ALM\u7cfb\u7edf\u5728\u4ece\u8fd9\u4e9b\u5de5\u5177\u4e2d\u62c9\u53d6\u89c2\u5bdf\u7ed3\u679c\u7684\u540c\u65f6\u89e6\u53d1LLM\u7684\u601d\u8003\u6d41\u7a0b\u3002\u5177\u4f53\u6765\u8bf4\uff0cLLM\u8c03\u7528\u5916\u90e8\u5de5\u5177\u8fdb\u884c\u63a8\u7406\uff0c\u83b7\u53d6\u5de5\u5177\u7684\u54cd\u5e94\u540e\u6682\u505c\uff0c\u7136\u540e\u6839\u636e\u6240\u6709\u5148\u524d\u7684\u54cd\u5e94\u4ee4\u724c\u51b3\u5b9a\u4e0b\u4e00\u6b65\u884c\u52a8\u3002\u5c3d\u7ba1\u8fd9\u79cd\u8303\u4f8b\u76f4\u63a5\u4e14\u6613\u4e8e\u5b9e\u65bd\uff0c\u4f46\u901a\u5e38\u4f1a\u56e0\u5197\u4f59\u63d0\u793a\u548c\u91cd\u590d\u6267\u884c\u5bfc\u81f4\u5de8\u5927\u7684\u8ba1\u7b97\u590d\u6742\u6027\u3002"}),"\n",(0,a.jsxs)(n.p,{children:["Flappy\u4f7f\u7528",(0,a.jsx)(n.a,{href:"https://arxiv.org/abs/2305.18323",children:"ReWOO"}),"\u4ee3\u66ff",(0,a.jsx)(n.a,{href:"https://arxiv.org/abs/2210.03629",children:"ReAct"}),"\u6765\u5c06LLM\u8f93\u51fa\u7684Token\u6570\u6700\u5c0f\u5316\u4ece\u800c\u964d\u4f4e\u6210\u672c\u3002\u5728\u6b64\u57fa\u7840\u4e4b\u4e0a\uff0cAgent\u5177\u5907\u4e86\u6839\u636e\u7528\u6237\u7684Prompt\u81ea\u4e3b\u8bbe\u8ba1\u6267\u884c\u8ba1\u5212\u7684\u80fd\u529b\u3002\u5b83\u9700\u786e\u5b9a\u4e00\u7cfb\u5217\u9700\u8981\u8c03\u7528\u7684\u51fd\u6570\uff0c\u4ee5\u6ee1\u8db3Prompt\u7684\u8981\u6c42\u3002\u968f\u540e\uff0c\u7cfb\u7edf\u6309\u7167\u8fd9\u4e2a\u7cbe\u5fc3\u5236\u5b9a\u7684\u8ba1\u5212\u8fdb\u884c\u6267\u884c\uff0c\u4ece\u800c\u8fdb\u4e00\u6b65\u63d0\u5347\u4e86\u6211\u4eec\u7cfb\u7edf\u7684\u6548\u7387\u3002"]}),"\n",(0,a.jsxs)(l.Z,{children:[(0,a.jsx)(s.Z,{value:"nodejs",label:"NodeJS (TypeScript)",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-ts",children:"agent.executePlan('Find the latest case with the plaintiff being families of victims and return its metadata.')\n"})})}),(0,a.jsx)(s.Z,{value:"java",label:"Java",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-java",children:"  Future<LawMetaReturn> future = lawAgent.executePlanAsync(LAW_EXECUTE_PLAN_PROMPT);\n"})})}),(0,a.jsx)(s.Z,{value:"kotlin",label:"Kotlin",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",children:"  lawAgent.use {\n    it.executePlan<LawMetaReturn>(LAW_EXECUTE_PLAN_PROMPT)\n  }\n"})})}),(0,a.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})})]}),"\n",(0,a.jsx)(n.h3,{id:"\u76f4\u63a5\u8c03\u7528-synthesized-function",children:"\u76f4\u63a5\u8c03\u7528 synthesized function"}),"\n",(0,a.jsx)(n.p,{children:"\u4f60\u4e5f\u53ef\u4ee5\u76f4\u63a5\u8c03\u7528sythesized function \u800c\u65e0\u9700\u521b\u5efa\u6216\u6267\u884c\u52a8\u4f5c\u8ba1\u5212\u3002"}),"\n",(0,a.jsxs)(l.Z,{children:[(0,a.jsx)(s.Z,{value:"nodejs",label:"NodeJS (TypeScript)",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-ts",children:"agent.callFunction('getMeta', {lawsuit: MOCK_LAWSUIT_DATA})\n"})})}),(0,a.jsx)(s.Z,{value:"java",label:"Java",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})}),(0,a.jsx)(s.Z,{value:"kotlin",label:"Kotlin",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})}),(0,a.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})})]}),"\n",(0,a.jsx)(n.h3,{id:"\u8c03\u7528-code-interpreter",children:"\u8c03\u7528 Code Interpreter"}),"\n",(0,a.jsx)(n.p,{children:"Code Interpreter \u5f53\u524d\u9700\u8981\u88ab\u76f4\u63a5\u8c03\u7528. We are working on a better way to integrate it with the agent."}),"\n",(0,a.jsxs)(l.Z,{children:[(0,a.jsx)(s.Z,{value:"nodejs",label:"NodeJS (TypeScript) with ChatGPT",default:!0,children:(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-ts",children:"agent.executePlan(\n  'There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs  and 150 heads in the barn?'\n)\n\n// \u4e5f\u53ef\u4ee5\u76f4\u63a5\u8c03\u7528\n\nagent.callFunction('pythonSandbox', {\n  code: 'There are some rabbits and chickens in a barn. What is the number of chickens if there are 396 legs and 150 heads in the barn?'\n})\n"})})}),(0,a.jsx)(s.Z,{value:"java",label:"Java",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})}),(0,a.jsx)(s.Z,{value:"kotlin",label:"Kotlin",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})}),(0,a.jsx)(s.Z,{value:"csharp",label:"C#",default:!0,children:(0,a.jsx)(n.p,{children:"Coming soon"})})]})]})}function p(e={}){const{wrapper:n}={...(0,r.ah)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(h,{...e})}):h(e)}},5318:(e,n,t)=>{t.d(n,{ah:()=>c});var a=t(7378);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function l(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function s(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?l(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):l(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function i(e,n){if(null==e)return{};var t,a,r=function(e,n){if(null==e)return{};var t,a,r={},l=Object.keys(e);for(a=0;a<l.length;a++)t=l[a],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(a=0;a<l.length;a++)t=l[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var o=a.createContext({}),c=function(e){var n=a.useContext(o),t=n;return e&&(t="function"==typeof e?e(n):s(s({},n),e)),t},d={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},u=a.forwardRef((function(e,n){var t=e.components,r=e.mdxType,l=e.originalType,o=e.parentName,u=i(e,["components","mdxType","originalType","parentName"]),h=c(t),p=r,f=h["".concat(o,".").concat(p)]||h[p]||d[p]||l;return t?a.createElement(f,s(s({ref:n},u),{},{components:t})):a.createElement(f,s({ref:n},u))}));u.displayName="MDXCreateElement"}}]);