(this["webpackJsonpn2o-demo-client"]=this["webpackJsonpn2o-demo-client"]||[]).push([[131],{3030:function(e,n){e.exports=function(e){var n={keyword:"and elif is global as in if from raise for except finally print import pass return exec else break not with class assert yield try while continue del or def lambda async await nonlocal|10 None True False",built_in:"Ellipsis NotImplemented"},a={className:"meta",begin:/^(>>>|\.\.\.) /},s={className:"subst",begin:/\{/,end:/\}/,keywords:n,illegal:/#/},i={className:"string",contains:[e.BACKSLASH_ESCAPE],variants:[{begin:/(u|b)?r?'''/,end:/'''/,contains:[e.BACKSLASH_ESCAPE,a],relevance:10},{begin:/(u|b)?r?"""/,end:/"""/,contains:[e.BACKSLASH_ESCAPE,a],relevance:10},{begin:/(fr|rf|f)'''/,end:/'''/,contains:[e.BACKSLASH_ESCAPE,a,s]},{begin:/(fr|rf|f)"""/,end:/"""/,contains:[e.BACKSLASH_ESCAPE,a,s]},{begin:/(u|r|ur)'/,end:/'/,relevance:10},{begin:/(u|r|ur)"/,end:/"/,relevance:10},{begin:/(b|br)'/,end:/'/},{begin:/(b|br)"/,end:/"/},{begin:/(fr|rf|f)'/,end:/'/,contains:[e.BACKSLASH_ESCAPE,s]},{begin:/(fr|rf|f)"/,end:/"/,contains:[e.BACKSLASH_ESCAPE,s]},e.APOS_STRING_MODE,e.QUOTE_STRING_MODE]},r={className:"number",relevance:0,variants:[{begin:e.BINARY_NUMBER_RE+"[lLjJ]?"},{begin:"\\b(0o[0-7]+)[lLjJ]?"},{begin:e.C_NUMBER_RE+"[lLjJ]?"}]},l={className:"params",begin:/\(/,end:/\)/,contains:["self",a,r,i]};return s.contains=[i,r,a],{aliases:["py","gyp"],keywords:n,illegal:/(<\/|->|\?)|=>/,contains:[a,r,i,e.HASH_COMMENT_MODE,{variants:[{className:"function",beginKeywords:"def"},{className:"class",beginKeywords:"class"}],end:/:/,illegal:/[${=;\n,]/,contains:[e.UNDERSCORE_TITLE_MODE,l,{begin:/->/,endsWithParent:!0,keywords:"None"}]},{className:"meta",begin:/^[\t ]*@/,end:/$/},{begin:/\b(print|exec)\(/}]}}}}]);
//# sourceMappingURL=react-syntax-highlighter_languages_highlight_python.9ac72e9c.chunk.js.map