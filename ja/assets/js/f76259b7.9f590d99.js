"use strict";(self.webpackChunk_pleisto_flappy_docs=self.webpackChunk_pleisto_flappy_docs||[]).push([[655],{8529:(e,n,r)=>{r.r(n),r.d(n,{assets:()=>o,contentTitle:()=>s,default:()=>p,frontMatter:()=>t,metadata:()=>c,toc:()=>d});var l=r(4246),i=r(5318);const t={sidebar_position:1,slug:"/"},s="\u30a4\u30f3\u30c8\u30ed\u30c0\u30af\u30b7\u30e7\u30f3",c={id:"introduction",title:"\u30a4\u30f3\u30c8\u30ed\u30c0\u30af\u30b7\u30e7\u30f3",description:"Flappy\u306f\u3001\u3042\u306a\u305f\u306e\u30d7\u30ed\u30b8\u30a7\u30af\u30c8\u3067AI\u306e\u7d71\u5408\u3092\u7c21\u6613\u5316\u3059\u308b\u305f\u3081\u306e\u672c\u756a\u74b0\u5883\u5bfe\u5fdc\u306e\u5927\u898f\u6a21\u8a00\u8a9e\u30e2\u30c7\u30eb\uff08LLM\uff09\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3/\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8SDK\u3067\u3059\u3002Flappy\u306f\u4f7f\u7528\u304c\u5bb9\u6613\u3067\u3001\u3069\u306e\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e\u3067\u3082\u4e92\u63db\u6027\u304c\u3042\u308a\u3001\u672c\u756a\u74b0\u5883\u306b\u5373\u3057\u3066\u4f7f\u7528\u3067\u304d\u308b\u30bd\u30ea\u30e5\u30fc\u30b7\u30e7\u30f3\u3067\u3042\u308a\u3001AI\u306e\u529b\u3092\u958b\u767a\u8005\u306e\u624b\u306b\u6e21\u3057\u307e\u3059\u3002",source:"@site/i18n/ja/docusaurus-plugin-content-docs/current/introduction.mdx",sourceDirName:".",slug:"/",permalink:"/ja/docs/",draft:!1,unlisted:!1,editUrl:"https://github.com/facebook/docusaurus/tree/main/packages/create-docusaurus/templates/shared/docs/introduction.mdx",tags:[],version:"current",sidebarPosition:1,frontMatter:{sidebar_position:1,slug:"/"},sidebar:"docSidebar",next:{title:"\u306f\u3058\u3081\u306b",permalink:"/ja/docs/quick-start"}},o={},d=[{value:"\u4e3b\u306a\u6a5f\u80fd",id:"\u4e3b\u306a\u6a5f\u80fd",level:2},{value:"Flappy\u3092\u9078\u3076\u7406\u7531\u306f\uff1f",id:"flappy\u3092\u9078\u3076\u7406\u7531\u306f",level:2},{value:"\u30b3\u30a2\u30b3\u30f3\u30dd\u30fc\u30cd\u30f3\u30c8",id:"\u30b3\u30a2\u30b3\u30f3\u30dd\u30fc\u30cd\u30f3\u30c8",level:2},{value:"\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8\u6a5f\u80fd",id:"\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8\u6a5f\u80fd",level:3},{value:"\u6a5f\u80fd\u5b9f\u88c5\u306e\u8a73\u7d30",id:"\u6a5f\u80fd\u5b9f\u88c5\u306e\u8a73\u7d30",level:4},{value:"LLM\u62bd\u8c61\u5316\u30ec\u30a4\u30e4\u30fc",id:"llm\u62bd\u8c61\u5316\u30ec\u30a4\u30e4\u30fc",level:3},{value:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308bLLM",id:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308bllm",level:2},{value:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308b\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e",id:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308b\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e",level:2}];function a(e){const n={code:"code",h1:"h1",h2:"h2",h3:"h3",h4:"h4",li:"li",ol:"ol",p:"p",strong:"strong",ul:"ul",...(0,i.ah)(),...e.components};return(0,l.jsxs)(l.Fragment,{children:[(0,l.jsx)(n.h1,{id:"\u30a4\u30f3\u30c8\u30ed\u30c0\u30af\u30b7\u30e7\u30f3",children:"\u30a4\u30f3\u30c8\u30ed\u30c0\u30af\u30b7\u30e7\u30f3"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306f\u3001\u3042\u306a\u305f\u306e\u30d7\u30ed\u30b8\u30a7\u30af\u30c8\u3067AI\u306e\u7d71\u5408\u3092\u7c21\u6613\u5316\u3059\u308b\u305f\u3081\u306e\u672c\u756a\u74b0\u5883\u5bfe\u5fdc\u306e\u5927\u898f\u6a21\u8a00\u8a9e\u30e2\u30c7\u30eb\uff08LLM\uff09\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3/\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8SDK\u3067\u3059\u3002Flappy\u306f\u4f7f\u7528\u304c\u5bb9\u6613\u3067\u3001\u3069\u306e\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e\u3067\u3082\u4e92\u63db\u6027\u304c\u3042\u308a\u3001\u672c\u756a\u74b0\u5883\u306b\u5373\u3057\u3066\u4f7f\u7528\u3067\u304d\u308b\u30bd\u30ea\u30e5\u30fc\u30b7\u30e7\u30f3\u3067\u3042\u308a\u3001AI\u306e\u529b\u3092\u958b\u767a\u8005\u306e\u624b\u306b\u6e21\u3057\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.h2,{id:"\u4e3b\u306a\u6a5f\u80fd",children:"\u4e3b\u306a\u6a5f\u80fd"}),"\n",(0,l.jsxs)(n.ul,{children:["\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"\u4f7f\u3044\u3084\u3059\u3055"}),"\uff1aFlappy\u306fCRUD\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u958b\u767a\u3068\u540c\u3058\u304f\u3089\u3044\u30e6\u30fc\u30b6\u30fc\u30d5\u30ec\u30f3\u30c9\u30ea\u30fc\u306b\u8a2d\u8a08\u3055\u308c\u3066\u304a\u308a\u3001AI\u521d\u5fc3\u8005\u306e\u958b\u767a\u8005\u306e\u5b66\u7fd2\u66f2\u7dda\u3092\u6700\u5c0f\u9650\u306b\u6291\u3048\u307e\u3059\u3002"]}),"\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"\u672c\u756a\u74b0\u5883\u5bfe\u5fdc"}),"\uff1a\u7814\u7a76\u3092\u8d85\u3048\u3066\u3001Flappy\u306f\u30b3\u30b9\u30c8\u52b9\u7387\u3068\u30b5\u30f3\u30c9\u30dc\u30c3\u30af\u30b9\u30bb\u30ad\u30e5\u30ea\u30c6\u30a3\u3092\u30d0\u30e9\u30f3\u30b9\u826f\u304f\u63d0\u4f9b\u3057\u3066\u3001\u5546\u696d\u74b0\u5883\u306b\u5b89\u5b9a\u3057\u305f\u30d7\u30e9\u30c3\u30c8\u30d5\u30a9\u30fc\u30e0\u3092\u63d0\u4f9b\u3059\u308b\u305f\u3081\u306e\u5805\u7262\u306aSDK\u3067\u3059\u3002"]}),"\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"\u8a00\u8a9e\u306b\u4f9d\u5b58\u3057\u306a\u3044"}),"\uff1aFlappy\u306f\u3042\u3089\u3086\u308b\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e\u3068\u30b7\u30fc\u30e0\u30ec\u30b9\u306b\u7d71\u5408\u3067\u304d\u3001\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u304c\u660e\u793a\u7684\u306bPython\u3092\u5fc5\u8981\u3068\u3057\u306a\u3044\u9650\u308a\u3001Python\u306e\u5fc5\u8981\u6027\u3092\u6392\u9664\u3057\u307e\u3059\u3002"]}),"\n"]}),"\n",(0,l.jsx)(n.h2,{id:"flappy\u3092\u9078\u3076\u7406\u7531\u306f",children:"Flappy\u3092\u9078\u3076\u7406\u7531\u306f\uff1f"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306f\u3001\u65e5\u5e38\u306e\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u3067\u5927\u898f\u6a21\u8a00\u8a9e\u30e2\u30c7\u30eb\uff08LLM\uff09\u306e\u53ef\u80fd\u6027\u3092\u89e3\u304d\u653e\u3064\u9375\u3067\u3059\u3002\u65b0\u305f\u306a\u30c7\u30fc\u30bf\u3092\u4f5c\u308a\u51fa\u3059\u3060\u3051\u3067\u306a\u304f\u3001\u65e2\u5b58\u306e\u30c7\u30fc\u30bf\u3092\u5909\u63db\u3057\u3001\u610f\u5473\u306e\u3042\u308b\u6d1e\u5bdf\u3092\u5f97\u308b\u305f\u3081\u306b\u5408\u6210\u3057\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.p,{children:"LLM\u306e\u5546\u696d\u4fa1\u5024\u306f\u3001\u696d\u754c\u7279\u6709\u306e\u30e6\u30fc\u30b9\u30b1\u30fc\u30b9\u3067\u306e\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u306b\u3042\u308a\u307e\u3059\u3002\u3057\u304b\u3057\u3001\u3053\u308c\u3089\u306e\u30e2\u30c7\u30eb\u3092\u65e2\u5b58\u306e\u30d3\u30b8\u30cd\u30b9\u30b7\u30b9\u30c6\u30e0\u306b\u7d71\u5408\u3059\u308b\u3053\u3068\u306f\u5927\u304d\u306a\u8ab2\u984c\u3067\u3059\u3002\u3053\u3053\u3067Flappy\u304c\u5149\u308a\u8f1d\u304d\u307e\u3059 - \u3059\u3079\u3066\u306e\u958b\u767a\u8005\u306e\u305f\u3081\u306e\u672c\u756a\u74b0\u5883\u5bfe\u5fdc\u306eLLM\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8SDK\u3068\u3057\u3066\u306e\u5f79\u5272\u3092\u679c\u305f\u3057\u307e\u3059\u3002\u305d\u308c\u306fLLM\u3068\u4ed6\u306e\u30bd\u30d5\u30c8\u30a6\u30a7\u30a2\u30b7\u30b9\u30c6\u30e0\u3068\u306e\u9593\u306e\u76f8\u4e92\u4f5c\u7528\u3092\u4fc3\u9032\u3057\u3001\u30c7\u30fc\u30bf\u4ea4\u63db\u306e\u52b9\u7387\u6027\u3092\u78ba\u4fdd\u3059\u308b\u305f\u3081\u306b\u3001\u9ad8\u3044\u7cbe\u5ea6\u3068\u7279\u5b9a\u306e\u51fa\u529b\u30d5\u30a9\u30fc\u30de\u30c3\u30c8\u304c\u5fc5\u8981\u3068\u306a\u308a\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306f\u5358\u306a\u308b\u7ffb\u8a33\u4ee5\u4e0a\u306e\u3082\u306e\u3067\u3059\u3002\u305d\u308c\u306f\u30a8\u30f3\u30d1\u30ef\u30fc\u30e1\u30f3\u30c8\u3067\u3059\u3002\u305d\u308c\u306f\u9ad8\u3044\u6280\u8853\u7684\u969c\u58c1\u3092\u53d6\u308a\u6255\u3044\u3001\u5e83\u7bc4\u3067\u8907\u96d1\u306a\u4f5c\u696d\u3092\u52b9\u7387\u7684\u306a\u30d7\u30ed\u30bb\u30b9\u306b\u5909\u3048\u307e\u3059\u3002Flappy\u3092\u7528\u3044\u308c\u3070\u3001AI\u306e\u30d0\u30c3\u30af\u30b0\u30e9\u30a6\u30f3\u30c9\u3092\u6301\u305f\u306a\u3044\u958b\u767a\u8005\u3067\u3082\u3001\u81ea\u5206\u305f\u3061\u306e\u30bd\u30d5\u30c8\u30a6\u30a7\u30a2\u306bAI\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u3092\u7d71\u5408\u3059\u308b\u3053\u3068\u304c\u3067\u304d\u3001\u5927\u898f\u6a21\u30e2\u30c7\u30eb\u306e\u696d\u754c\u7279\u6709\u306e\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u306b\u5bc4\u4e0e\u3059\u308b\u3053\u3068\u304c\u3067\u304d\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306fAutoGPT\u3001TypeChat\u3001LangChain\u306a\u3069\u306e\u4ee3\u66ff\u54c1\u3092\u8d85\u3048\u3066\u3001\u672c\u756a\u74b0\u5883\u306b\u5373\u3057\u305f\u30bd\u30ea\u30e5\u30fc\u30b7\u30e7\u30f3\u3092\u63d0\u4f9b\u3057\u307e\u3059\u3002\u30ea\u30a2\u30eb\u30ef\u30fc\u30eb\u30c9\u306e\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u3092\u8003\u616e\u306b\u5165\u308c\u305f\u8a2d\u8a08\u3067\u3001Flappy\u306f\u5b89\u5168\u6027\u3001\u30b3\u30b9\u30c8\u52b9\u679c\u3001\u5805\u7262\u6027\u3001\u7dad\u6301\u53ef\u80fd\u6027\u3092\u512a\u5148\u3057\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.p,{children:"\u8981\u3059\u308b\u306b\u3001Flappy\u306f\u5358\u306a\u308b\u30c4\u30fc\u30eb\u4ee5\u4e0a\u306e\u3082\u306e\u3067\u3059\u3002\u305d\u308c\u306fAI\u5206\u91ce\u306e\u9769\u547d\u3067\u3042\u308a\u3001\u53ef\u80fd\u6027\u3068\u5b9f\u7528\u7684\u306a\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u306e\u9593\u306e\u30ae\u30e3\u30c3\u30d7\u3092\u57cb\u3081\u307e\u3059\u3002\u305d\u308c\u306fAI\u306e\u529b\u3092\u3059\u3079\u3066\u306e\u958b\u767a\u8005\u306e\u624b\u306b\u6e21\u3057\u3001\u3055\u307e\u3056\u307e\u306a\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u3067AI\u99c6\u52d5\u306e\u9769\u65b0\u3092\u63a8\u9032\u3057\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u3092\u9078\u3073\u3001\u3053\u306e\u9769\u547d\u306e\u4e00\u90e8\u3068\u306a\u308a\u3001AI\u306e\u5168\u6f5c\u529b\u3092\u5f15\u304d\u51fa\u3057\u3001AI\u306e\u672a\u6765\u3092\u5171\u306b\u5f62\u6210\u3057\u307e\u3057\u3087\u3046\u3002"}),"\n",(0,l.jsx)(n.h2,{id:"\u30b3\u30a2\u30b3\u30f3\u30dd\u30fc\u30cd\u30f3\u30c8",children:"\u30b3\u30a2\u30b3\u30f3\u30dd\u30fc\u30cd\u30f3\u30c8"}),"\n",(0,l.jsx)(n.h3,{id:"\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8\u6a5f\u80fd",children:"\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8\u6a5f\u80fd"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306e\u30a8\u30b3\u30b7\u30b9\u30c6\u30e0\u3067\u306f\u3001\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8\u306fLLM\u306e\u591a\u6a5f\u80fd\u306a\u4f1d\u9054\u8def\u3068\u3057\u3066\u6a5f\u80fd\u3057\u3001\u30c7\u30fc\u30bf\u306e\u69cb\u9020\u5316\u3001\u5916\u90e8API\u306e\u547c\u3073\u51fa\u3057\u3001\u307e\u305f\u306f\u5fc5\u8981\u306b\u5fdc\u3058\u3066LLM\u751f\u6210\u306ePython\u30b3\u30fc\u30c9\u306e\u30b5\u30f3\u30c9\u30dc\u30c3\u30af\u30b9\u5316\u306a\u3069\u3001\u69d8\u3005\u306a\u30bf\u30b9\u30af\u3092\u5b9f\u884c\u3057\u307e\u3059\u3002\u3053\u306e\u8a2d\u8a08\u54f2\u5b66\u306f\u3001\u5404\u7a2e\u30bb\u30af\u30bf\u30fc\u3067LLM\u30d9\u30fc\u30b9\u306eAI\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u306e\u9700\u8981\u5897\u52a0\u306b\u5fdc\u3048\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306e\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8\u6a5f\u80fd\u306f\u3001\u4ee5\u4e0b\u306e2\u3064\u306e\u57fa\u672c\u7684\u306a\u30bf\u30a4\u30d7\u306b\u57fa\u3065\u3044\u3066\u69cb\u7bc9\u3055\u308c\u3066\u3044\u307e\u3059\uff1a"}),"\n",(0,l.jsxs)(n.ol,{children:["\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"InvokeFunction"}),"\uff1a\u3053\u306e\u6a5f\u80fd\u306f\u30a8\u30fc\u30b8\u30a7\u30f3\u30c8\u304c\u74b0\u5883\u3068\u5bfe\u8a71\u3059\u308b\u3053\u3068\u3092\u53ef\u80fd\u306b\u3057\u307e\u3059\u3002\u3053\u308c\u306f\u5165\u529b\u3068\u51fa\u529b\u306e\u30d1\u30e9\u30e1\u30fc\u30bf\u3067\u5b9a\u7fa9\u3055\u308c\u3001LLM\u3068\u306e\u52b9\u7387\u7684\u306a\u5bfe\u8a71\u3092\u4fc3\u9032\u3057\u307e\u3059\u3002"]}),"\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"SynthesizedFunction"}),"\uff1a\u3053\u306e\u6a5f\u80fd\u306fLLM\u306b\u3088\u3063\u3066\u51e6\u7406\u3055\u308c\u3001\u305d\u306e\u8aac\u660e\u3068\u5165\u51fa\u529b\u306e\u69cb\u9020\u306e\u5b9a\u7fa9\u3060\u3051\u304c\u5fc5\u8981\u3067\u3059\u3002"]}),"\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"CodeInterpreter"})," Flappy\u306e\u30b3\u30fc\u30c9\u30a4\u30f3\u30bf\u30d7\u30ea\u30bf\u306f\u3001LLM\u304c\u751f\u6210\u3057\u305fPython\u307e\u305f\u306fTypeScript\u30b3\u30fc\u30c9\u3092\u5b9f\u884c\u3059\u308b\u305f\u3081\u306e\u5b89\u5168\u306a\u74b0\u5883\u3068\u3057\u3066\u6a5f\u80fd\u3057\u307e\u3059\u3002\u3053\u308c\u306f\u30e9\u30f3\u30bf\u30a4\u30e0\u30a8\u30e9\u30fc\u3084\u6f5c\u5728\u7684\u306a\u30bb\u30ad\u30e5\u30ea\u30c6\u30a3\u8106\u5f31\u6027\u3092\u6e1b\u3089\u3059\u30b5\u30f3\u30c9\u30dc\u30c3\u30af\u30b9\u5316\u3055\u308c\u305f\u5b89\u5168\u6a5f\u80fd\u3092\u63d0\u4f9b\u3057\u3001\u672c\u756a\u74b0\u5883\u3067\u306e\u30c7\u30d7\u30ed\u30a4\u306b\u9069\u3057\u3066\u3044\u307e\u3059\u3002"]}),"\n"]}),"\n",(0,l.jsx)(n.h4,{id:"\u6a5f\u80fd\u5b9f\u88c5\u306e\u8a73\u7d30",children:"\u6a5f\u80fd\u5b9f\u88c5\u306e\u8a73\u7d30"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306f\u3053\u308c\u3089\u306e\u6a5f\u80fd\u3092\u5f37\u5316\u3059\u308b\u305f\u3081\u306e\u72ec\u7279\u306a\u5b9f\u88c5\u30e1\u30ab\u30cb\u30ba\u30e0\u3092\u5c0e\u5165\u3057\u307e\u3059\uff1a"}),"\n",(0,l.jsxs)(n.ul,{children:["\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"\u72ec\u81ea\u306eJSON\u30b9\u30ad\u30fc\u30de\u7d71\u5408"}),"\uff1a\u30e6\u30fc\u30b6\u30fc\u306f\u3001Flappy\u304cJSON\u30b9\u30ad\u30fc\u30de\u306b\u5909\u63db\u3059\u308b\u62bd\u8c61\u30af\u30e9\u30b9\u3092\u597d\u307f\u306e\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e\u3067\u5b9a\u7fa9\u3067\u304d\u307e\u3059\u3002\u3053\u306e\u6a5f\u80fd\u306f\u3001\u30de\u30b7\u30f3\u306e\u53ef\u8aad\u6027\u3068\u76f8\u4e92\u904b\u7528\u6027\u3092\u5f37\u5316\u3057\u3001LLM\u306e\u5165\u529b\u3068\u51fa\u529b\u3092\u7ba1\u7406\u3057\u3001\u5236\u5fa1\u3055\u308c\u305f\u4e00\u8cab\u6027\u306e\u3042\u308b\u30de\u30b7\u30f3\u53ef\u8aad\u306a\u30c7\u30fc\u30bf\u3092\u63d0\u4f9b\u3057\u307e\u3059\u3002"]}),"\n",(0,l.jsxs)(n.li,{children:[(0,l.jsx)(n.strong,{children:"AST\u30d1\u30fc\u30b7\u30f3\u30b0"}),"\uff1aFlappy\u306fLLM\u306e\u51fa\u529b\u306b\u5bfe\u3057\u3066\u62bd\u8c61\u69cb\u6587\u6728\uff08AST\uff09\u30d1\u30fc\u30b7\u30f3\u30b0\u3092\u884c\u3044\u3001\u751f\u6210\u3055\u308c\u305fJSON\u30c7\u30fc\u30bf\u304c\u5b9a\u7fa9\u3055\u308c\u305fJSON\u30b9\u30ad\u30fc\u30de\u30b9\u30ad\u30fc\u30de\u306b\u53b3\u5bc6\u306b\u6e96\u62e0\u3059\u308b\u3053\u3068\u3092\u4fdd\u8a3c\u3057\u307e\u3059\u3002"]}),"\n"]}),"\n",(0,l.jsx)(n.h3,{id:"llm\u62bd\u8c61\u5316\u30ec\u30a4\u30e4\u30fc",children:"LLM\u62bd\u8c61\u5316\u30ec\u30a4\u30e4\u30fc"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u306f\u3001\u3055\u307e\u3056\u307e\u306aLLM\u9593\u3067\u7c21\u5358\u306b\u5207\u308a\u66ff\u3048\u308b\u3053\u3068\u304c\u3067\u304d\u3001\u30d5\u30a9\u30fc\u30eb\u30d0\u30c3\u30afLLM\u3092\u6307\u5b9a\u3059\u308b\u3053\u3068\u3067\u3001\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u306e\u5b89\u5b9a\u6027\u3092\u78ba\u4fdd\u3059\u308b\u305f\u3081\u306e\u62bd\u8c61\u5316\u30ec\u30a4\u30e4\u30fc\u3092\u63d0\u4f9b\u3057\u307e\u3059\u3002"}),"\n",(0,l.jsx)(n.p,{children:"Flappy\u3092\u4f7f\u7528\u3059\u308b\u3068\u3001\u958b\u767a\u8005\u306f\u8a00\u8a9e\u306b\u4f9d\u5b58\u3057\u306a\u3044\u65b9\u6cd5\u3067LLM\u30d9\u30fc\u30b9\u306e\u30a2\u30d7\u30ea\u30b1\u30fc\u30b7\u30e7\u30f3\u3092\u69cb\u7bc9\u3059\u308b\u3053\u3068\u304c\u3067\u304d\u307e\u3059\u3002\u3042\u306a\u305f\u306e\u597d\u304d\u306a\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e\u3067AI\u306e\u81a8\u5927\u306a\u53ef\u80fd\u6027\u3092\u5f15\u304d\u51fa\u3059\u305f\u3081\u306b\u3001\u4eca\u65e5\u304b\u3089Flappy\u3067\u306e\u65c5\u3092\u958b\u59cb\u3057\u307e\u3057\u3087\u3046\u3002"}),"\n",(0,l.jsx)(n.h2,{id:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308bllm",children:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308bLLM"}),"\n",(0,l.jsxs)(n.ul,{children:["\n",(0,l.jsx)(n.li,{children:"OpenAI ChatGPT & GPT-4"}),"\n",(0,l.jsx)(n.li,{children:"Baichuan-53B"}),"\n",(0,l.jsxs)(n.li,{children:["HuggingFace Transformers\uff08Llama\u3001mistral\u306a\u3069\uff09",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n",(0,l.jsxs)(n.li,{children:["Google PaLM2 ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n",(0,l.jsxs)(n.li,{children:["Baidu ERNIE ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n",(0,l.jsxs)(n.li,{children:["iFly XingHuo ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n",(0,l.jsxs)(n.li,{children:["MiniMax ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n"]}),"\n",(0,l.jsx)(n.h2,{id:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308b\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e",children:"\u30b5\u30dd\u30fc\u30c8\u3057\u3066\u3044\u308b\u30d7\u30ed\u30b0\u30e9\u30df\u30f3\u30b0\u8a00\u8a9e"}),"\n",(0,l.jsxs)(n.ul,{children:["\n",(0,l.jsx)(n.li,{children:"Node.js (JavaScript/TypeScript)"}),"\n",(0,l.jsx)(n.li,{children:"Kotlin & Java"}),"\n",(0,l.jsx)(n.li,{children:"C# (.NET Core)"}),"\n",(0,l.jsxs)(n.li,{children:["Go ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n",(0,l.jsxs)(n.li,{children:["PHP ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n",(0,l.jsxs)(n.li,{children:["Ruby ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n",(0,l.jsxs)(n.li,{children:["Python ",(0,l.jsx)(n.code,{children:"\u8fd1\u65e5\u516c\u958b"})]}),"\n"]})]})}function p(e={}){const{wrapper:n}={...(0,i.ah)(),...e.components};return n?(0,l.jsx)(n,{...e,children:(0,l.jsx)(a,{...e})}):a(e)}},5318:(e,n,r)=>{r.d(n,{ah:()=>d});var l=r(7378);function i(e,n,r){return n in e?Object.defineProperty(e,n,{value:r,enumerable:!0,configurable:!0,writable:!0}):e[n]=r,e}function t(e,n){var r=Object.keys(e);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);n&&(l=l.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),r.push.apply(r,l)}return r}function s(e){for(var n=1;n<arguments.length;n++){var r=null!=arguments[n]?arguments[n]:{};n%2?t(Object(r),!0).forEach((function(n){i(e,n,r[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(r)):t(Object(r)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(r,n))}))}return e}function c(e,n){if(null==e)return{};var r,l,i=function(e,n){if(null==e)return{};var r,l,i={},t=Object.keys(e);for(l=0;l<t.length;l++)r=t[l],n.indexOf(r)>=0||(i[r]=e[r]);return i}(e,n);if(Object.getOwnPropertySymbols){var t=Object.getOwnPropertySymbols(e);for(l=0;l<t.length;l++)r=t[l],n.indexOf(r)>=0||Object.prototype.propertyIsEnumerable.call(e,r)&&(i[r]=e[r])}return i}var o=l.createContext({}),d=function(e){var n=l.useContext(o),r=n;return e&&(r="function"==typeof e?e(n):s(s({},n),e)),r},a={inlineCode:"code",wrapper:function(e){var n=e.children;return l.createElement(l.Fragment,{},n)}},p=l.forwardRef((function(e,n){var r=e.components,i=e.mdxType,t=e.originalType,o=e.parentName,p=c(e,["components","mdxType","originalType","parentName"]),h=d(r),j=i,u=h["".concat(o,".").concat(j)]||h[j]||a[j]||t;return r?l.createElement(u,s(s({ref:n},p),{},{components:r})):l.createElement(u,s({ref:n},p))}));p.displayName="MDXCreateElement"}}]);