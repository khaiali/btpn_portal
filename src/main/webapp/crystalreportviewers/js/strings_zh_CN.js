// LOCALIZATION STRING

// Strings for calendar.js and calendar_param.js
var L_Today     = "\u4ECA\u5929";
var L_January   = "\u4E00\u6708";
var L_February  = "\u4E8C\u6708";
var L_March     = "\u4E09\u6708";
var L_April     = "\u56DB\u6708";
var L_May       = "\u4E94\u6708";
var L_June      = "\u516D\u6708";
var L_July      = "\u4E03\u6708";
var L_August    = "\u516B\u6708";
var L_September = "\u4E5D\u6708";
var L_October   = "\u5341\u6708";
var L_November  = "\u5341\u4E00\u6708";
var L_December  = "\u5341\u4E8C\u6708";
var L_Su        = "\u65E5";
var L_Mo        = "\u4E00";
var L_Tu        = "\u4E8C";
var L_We        = "\u4E09";
var L_Th        = "\u56DB";
var L_Fr        = "\u4E94";
var L_Sa        = "\u516D";

// strings for dt_param.js
var L_TIME_SEPARATOR = ":";
var L_AM_DESIGNATOR = "\u4E0A\u5348";
var L_PM_DESIGNATOR = "\u4E0B\u5348";

// strings for range parameter
var L_FROM = "From {0}";
var L_TO = "To {0}";
var L_AFTER = "After {0}";
var L_BEFORE = "Before {0}";
var L_FROM_TO = "From {0} to {1}";
var L_FROM_BEFORE = "From {0} to before {1}";
var L_AFTER_TO = "After {0} to {1}";
var L_AFTER_BEFORE = "After {0} to before {1}";

// Strings for prompts.js and prompts_param.js
var L_BadNumber     = "This parameter is of type \"Number\" and can only contain a negative sign symbol, digits (\"0-9\"), digit grouping symbols or a decimal symbol. Please correct the entered parameter value.";
var L_BadCurrency   = "This parameter is of type \"Currency\" and can only contain a negative sign symbol, digits (\"0-9\"), digit grouping symbols or a decimal symbol. Please correct the entered parameter value.";
var L_BadDate       = "This parameter is of type \"Date\" and should be in the format \"Date(yyyy,mm,dd)\" where \"yyyy\" is the four digit year, \"mm\" is the month (e.g. January = 1), and \"dd\" is the number of days into the given month.";
var L_BadDateTime   = "This parameter is of type \"DateTime\" and the correct format is \"DateTime(yyyy,mm,dd,hh,mm,ss)\". \"yyyy\" is the four digit year, \"mm\" is the month (e.g. January = 1), \"dd\" is the day of the month, \"hh\" is hours in a 24 hour clock, \"mm\" is minutes and \"ss\" is seconds.";
var L_BadTime       = "This parameter is of type \"Time\" and should be in the format \"Time(hh,mm,ss)\" where \"hh\" is hours in a 24 hour clock, \"mm\" is minutes into the hour, and \"ss\" is seconds into the minute.";
var L_NoValue       = "\u65E0\u503C";
var L_BadValue      = "\u8981\u8BBE\u7F6E\u201C\u65E0\u503C\u201D\uFF0C\u5FC5\u987B\u5C06\u201C\u4ECE\u201D\u548C\u201C\u5230\u201D\u503C\u540C\u65F6\u8BBE\u7F6E\u4E3A\u201C\u65E0\u503C\u201D\u3002";
var L_BadBound      = "\u65E0\u6CD5\u540C\u65F6\u8BBE\u7F6E\u201C\u65E0\u4E0B\u9650\u201D\u548C\u201C\u65E0\u4E0A\u9650\u201D\u3002";
var L_NoValueAlready = "\u6B64\u53C2\u6570\u5DF2\u8BBE\u7F6E\u4E3A\u201C\u65E0\u503C\u201D\uFF0C\u5728\u6DFB\u52A0\u5176\u4ED6\u503C\u4E4B\u524D\uFF0C\u8BF7\u53D6\u6D88\u201C\u65E0\u503C\u201D\u3002";
var L_RangeError    = "\u8303\u56F4\u8D77\u59CB\u503C\u4E0D\u80FD\u5927\u4E8E\u8303\u56F4\u7ED3\u675F\u503C\u3002";
var L_NoDateEntered = "\u5FC5\u987B\u8F93\u5165\u65E5\u671F\u3002";

// Strings for ../html/crystalexportdialog.htm
var L_ExportOptions     = "\u5BFC\u51FA\u9009\u9879";
var L_PrintOptions      = "\u6253\u5370\u9009\u9879";
var L_PrintPageTitle    = "\u6253\u5370\u62A5\u8868";
var L_ExportPageTitle   = "\u5BFC\u51FA\u62A5\u8868";
var L_OK                = "\u786E\u5B9A";
var L_Cancel            = "\u53D6\u6D88";
var L_PrintPageRange    = "\u8F93\u5165\u60F3\u8981\u6253\u5370\u7684\u9875\u7801\u8303\u56F4\u3002";
var L_ExportPageRange   = "\u8F93\u5165\u60F3\u8981\u5BFC\u51FA\u7684\u9875\u7801\u8303\u56F4\u3002";
var L_InvalidPageRange  = "The page range value(s) are incorrect.  Please enter a valid page range.";
var L_ExportFormat      = "\u8BF7\u4ECE\u5217\u8868\u4E2D\u9009\u62E9\u5BFC\u51FA\u683C\u5F0F\u3002";
var L_Formats           = "\u6587\u4EF6\u683C\u5F0F\uFF1A";
var L_PageRange         = "\u9875\u7801\u8303\u56F4\uFF1A";
var L_All               = "\u5168\u90E8";
var L_Pages             = "\u9875\uFF1A";
var L_From              = "\u4ECE\uFF1A";
var L_To                = "\u5230\uFF1A";
var L_PrintStep0        = "\u82E5\u8981\u6253\u5370\uFF1A";
var L_PrintStep1        = "1.  \u5728\u968F\u540E\u663E\u793A\u7684\u5BF9\u8BDD\u6846\u4E2D\uFF0C\u9009\u62E9\u201C\u6253\u5F00\u8BE5\u6587\u4EF6\u201D\u9009\u9879\uFF0C\u7136\u540E\u5355\u51FB\u201C\u786E\u5B9A\u201D\u6309\u94AE\u3002";
var L_PrintStep2        = "2.  \u5355\u51FB Acrobat Reader \u83DC\u5355\u4E0A\u7684\u6253\u5370\u673A\u56FE\u6807\uFF0C\u800C\u4E0D\u8981\u5355\u51FB Internet \u6D4F\u89C8\u5668\u4E0A\u7684\u6253\u5370\u6309\u94AE\u3002";
var L_RTFFormat         = "Rich Text Format (RTF)";
var L_AcrobatFormat     = "PDF";
var L_CrystalRptFormat  = "Crystal Reports (RPT)";
var L_WordFormat        = "Microsoft Word (97-2003)";
var L_ExcelFormat       = "Microsoft Excel (97-2003)";
var L_ExcelRecordFormat = "Microsoft Excel (97-2003) Data-Only";
var L_EditableRTFFormat = "Microsoft Word (97-2003) - Editable";

// Strings for print.js
var L_PrintControlInstallError = "\u5B89\u88C5 ActiveX \u6253\u5370\u63A7\u4EF6\u5931\u8D25\u3002\u9519\u8BEF\u4EE3\u7801\uFF1A";
var L_PrintControlPlugin = "Crystal Reports ActiveX \u6253\u5370\u63A7\u4EF6\u63D2\u4EF6";

// Strings for previewerror.js
var L_SessionExpired = "\u4F1A\u8BDD\u5DF2\u8D85\u65F6\u3002";
var L_PleaseRefresh = "\u8BF7\u6309\u201C\u5237\u65B0\u201D\u6309\u94AE\u5E76\u91CD\u8BD5\u3002";
