package com.f1._jsps;

import com.f1.http.*;
import com.f1.utils.*;
import com.f1.http.handler.AbstractHttpHandler;
import com.f1.http.HttpUtils;
import static com.f1.http.HttpUtils.*;

public class amiweb_ext_searchbox_js_1 extends AbstractHttpHandler{

	public amiweb_ext_searchbox_js_1() {
	}
  
	public boolean canHandle(HttpRequestResponse request){
	  return true;
	}

	public void handle(HttpRequestResponse request) throws java.io.IOException{
	  super.handle(request);
	  com.f1.utils.FastPrintStream out = request.getOutputStream();
	  HttpSession session = request.getSession(false);
	  HttpServer server = request.getHttpServer();
	  LocaleFormatter formatter = session == null ? server.getHttpSessionManager().getDefaultFormatter() : session.getFormatter();
          out.print(
            "/* ***** BEGIN LICENSE BLOCK *****\r\n"+
            " * Distributed under the BSD license:\r\n"+
            " *\r\n"+
            " * Copyright (c) 2010, Ajax.org B.V.\r\n"+
            " * All rights reserved.\r\n"+
            " *\r\n"+
            " * Redistribution and use in source and binary forms, with or without\r\n"+
            " * modification, are permitted provided that the following conditions are met:\r\n"+
            " *     * Redistributions of source code must retain the above copyright\r\n"+
            " *       notice, this list of conditions and the following disclaimer.\r\n"+
            " *     * Redistributions in binary form must reproduce the above copyright\r\n"+
            " *       notice, this list of conditions and the following disclaimer in the\r\n"+
            " *       documentation and/or other materials provided with the distribution.\r\n"+
            " *     * Neither the name of Ajax.org B.V. nor the\r\n"+
            " *       names of its contributors may be used to endorse or promote products\r\n"+
            " *       derived from this software without specific prior written permission.\r\n"+
            " *\r\n"+
            " * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS \"AS IS\" AND\r\n"+
            " * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED\r\n"+
            " * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE\r\n"+
            " * DISCLAIMED. IN NO EVENT SHALL AJAX.ORG B.V. BE LIABLE FOR ANY\r\n"+
            " * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES\r\n"+
            " * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;\r\n"+
            " * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND\r\n"+
            " * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT\r\n"+
            " * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS\r\n"+
            " * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.\r\n"+
            " *\r\n"+
            " * ***** END LICENSE BLOCK ***** */\r\n"+
            "\r\n"+
            "ace.define('ace/ext/searchbox', ['require', 'exports', 'module' , 'ace/lib/dom', 'ace/lib/lang', 'ace/lib/event', 'ace/keyboard/hash_handler', 'ace/lib/keys'], function(require, exports, module) {\r\n"+
            "\r\n"+
            "\r\n"+
            "var dom = require(\"../lib/dom\");\r\n"+
            "var lang = require(\"../lib/lang\");\r\n"+
            "var event = require(\"../lib/event\");\r\n"+
            "var searchboxCss = \"\\\r\n"+
            "/* ------------------------------------------------------------------------------------------\\\r\n"+
            "* Editor Search Form\\\r\n"+
            "* --------------------------------------------------------------------------------------- */\\\r\n"+
            ".ace_search {\\\r\n"+
            "background-color: #ddd;\\\r\n"+
            "border: 1px solid #cbcbcb;\\\r\n"+
            "border-top: 0 none;\\\r\n"+
            "max-width: 297px;\\\r\n"+
            "overflow: hidden;\\\r\n"+
            "margin: 0;\\\r\n"+
            "padding: 4px;\\\r\n"+
            "padding-right: 6px;\\\r\n"+
            "padding-bottom: 0;\\\r\n"+
            "position: absolute;\\\r\n"+
            "top: 0px;\\\r\n"+
            "z-index: 99;\\\r\n"+
            "white-space: normal;\\\r\n"+
            "}\\\r\n"+
            ".ace_search.left {\\\r\n"+
            "border-left: 0 none;\\\r\n"+
            "border-radius: 0px 0px 5px 0px;\\\r\n"+
            "left: 0;\\\r\n"+
            "}\\\r\n"+
            ".ace_search.right {\\\r\n"+
            "border-radius: 0px 0px 0px 5px;\\\r\n"+
            "border-right: 0 none;\\\r\n"+
            "right: 0;\\\r\n"+
            "width: 100%;\\\r\n"+
            "height: 55px;\\\r\n"+
            "}\\\r\n"+
            ".ace_search_form, .ace_replace_form {\\\r\n"+
            "border-radius: 3px;\\\r\n"+
            "border: 1px solid #cbcbcb;\\\r\n"+
            "float: left;\\\r\n"+
            "margin-bottom: 4px;\\\r\n"+
            "overflow: hidden;\\\r\n"+
            "margin-top: 20px;\\\r\n"+
            "}\\\r\n"+
            ".ace_search_form.ace_nomatch {\\\r\n"+
            "outline: 1px solid red;\\\r\n"+
            "}\\\r\n"+
            ".ace_search_field {\\\r\n"+
            "background-color: white;\\\r\n"+
            "border-right: 1px solid #cbcbcb;\\\r\n"+
            "border: 0 none;\\\r\n"+
            "-webkit-box-sizing: border-box;\\\r\n"+
            "-moz-box-sizing: border-box;\\\r\n"+
            "box-sizing: border-box;\\\r\n"+
            "display: block;\\\r\n"+
            "float: left;\\\r\n"+
            "height: 22px;\\\r\n"+
            "outline: 0;\\\r\n"+
            "padding: 0 7px;\\\r\n"+
            "width: 214px;\\\r\n"+
            "margin: 0;\\\r\n"+
            "}\\\r\n"+
            ".ace_searchbtn,\\\r\n"+
            ".ace_replacebtn {\\\r\n"+
            "background: #fff;\\\r\n"+
            "border: 0 none;\\\r\n"+
            "border-left: 1px solid #dcdcdc;\\\r\n"+
            "cursor: pointer;\\\r\n"+
            "display: block;\\\r\n"+
            "float: left;\\\r\n"+
            "height: 22px;\\\r\n"+
            "margin: 0;\\\r\n"+
            "padding: 0;\\\r\n"+
            "position: relative;\\\r\n"+
            "}\\\r\n"+
            ".ace_searchbtn:last-child,\\\r\n"+
            ".ace_replacebtn:last-child {\\\r\n"+
            "border-top-right-radius: 3px;\\\r\n"+
            "border-bottom-right-radius: 3px;\\\r\n"+
            "}\\\r\n"+
            ".ace_searchbtn:disabled {\\\r\n"+
            "background: none;\\\r\n"+
            "cursor: default;\\\r\n"+
            "}\\\r\n"+
            ".ace_searchbtn {\\\r\n"+
            "background-position: 50% 50%;\\\r\n"+
            "background-repeat: no-repeat;\\\r\n"+
            "width: 27px;\\\r\n"+
            "}\\\r\n"+
            ".ace_searchbtn.prev {\\\r\n"+
            "background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAFCAYAAAB4ka1VAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAADFJREFUeNpiSU1NZUAC/6E0I0yACYskCpsJiySKIiY0SUZk40FyTEgCjGgKwTRAgAEAQJUIPCE+qfkAAAAASUVORK5CYII=);    \\\r\n"+
            "}\\\r\n"+
            ".ace_searchbtn.next {\\\r\n"+
            "background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAgAAAAFCAYAAAB4ka1VAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAADRJREFUeNpiTE1NZQCC/0DMyIAKwGJMUAYDEo3M/s+EpvM/mkKwCQxYjIeLMaELoLMBAgwAU7UJObTKsvAAAAAASUVORK5CYII=);    \\\r\n"+
            "}\\\r\n"+
            ".ace_searchbtn_close {\\\r\n"+
            "background: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAcCAYAAABRVo5BAAAAZ0lEQVR42u2SUQrAMAhDvazn8OjZBilCkYVVxiis8H4CT0VrAJb4WHT3C5xU2a2IQZXJjiQIRMdkEoJ5Q2yMqpfDIo+XY4k6h+YXOyKqTIj5REaxloNAd0xiKmAtsTHqW8sR2W5f7gCu5nWFUpVjZwAAAABJRU5ErkJggg==) no-repeat 50% 0;\\\r\n"+
            "border-radius: 50%;\\\r\n"+
            "border: 0 none;\\\r\n"+
            "color: #656565;\\\r\n"+
            "cursor: pointer;\\\r\n"+
            "display: block;\\\r\n"+
            "float: right;\\\r\n"+
            "font-family: Arial;\\\r\n"+
            "font-size: 16px;\\\r\n"+
            "height: 11px;\\\r\n"+
            "line-height: 16px;\\\r\n"+
            "margin: -1px -11px 4px 2px;\\\r\n"+
            "padding: 0;\\\r\n"+
            "text-align: center;\\\r\n"+
            "width: 11px;\\\r\n"+
            "}\\\r\n"+
            ".ace_replacebtn.prev {\\\r\n"+
            "width: 54px\\\r\n"+
            "}\\\r\n"+
            ".ace_replacebtn.next {\\\r\n"+
            "width: 27px\\\r\n"+
            "}\\\r\n"+
            ".ace_button {\\\r\n"+
            "margin-left: 2px;\\\r\n"+
            "cursor: pointer;\\\r\n"+
            "-webkit-user-select: none;\\\r\n"+
            "-moz-user-select: none;\\\r\n"+
            "-o-user-select: none;\\\r\n"+
            "-ms-user-select: none;\\\r\n"+
            "user-select: none;\\\r\n"+
            "overflow: hidden;\\\r\n"+
            "opacity: 0.7;\\\r\n"+
            "border: 1px solid rgba(100,100,100,0.23);\\\r\n"+
            "padding: 1px;\\\r\n"+
            "-moz-box-sizing: border-box;\\\r\n"+
            "box-sizing:    border-box;\\\r\n"+
            "color: black;\\\r\n"+
            "}\\\r\n"+
            ".ace_button:hover {\\\r\n"+
            "background-color: #eee;\\\r\n"+
            "opacity:1;\\\r\n"+
            "}\\\r\n"+
            ".ace_button:active {\\\r\n"+
            "background-color: #ddd;\\\r\n"+
            "}\\\r\n"+
            ".ace_button.checked {\\\r\n"+
            "border-color: #3399ff;\\\r\n"+
            "opacity:1;\\\r\n"+
            "}\\\r\n"+
            ".ace_search_options{\\\r\n"+
            "margin-bottom: 3px;\\\r\n"+
            "text-align: right;\\\r\n"+
            "-webkit-user-select: none;\\\r\n"+
            "-moz-user-select: none;\\\r\n"+
            "-o-user-select: none;\\\r\n"+
            "-ms-user-select: none;\\\r\n"+
            "user-select: none;\\\r\n"+
            "}\";\r\n"+
            "var HashHandler = require(\"../keyboard/hash_handler\").HashHandler;\r\n"+
            "var keyUtil = require(\"../lib/keys\");\r\n"+
            "\r\n"+
            "dom.importCssString(searchboxCss, \"ace_searchbox\");\r\n"+
            "\r\n"+
            "var html = '<div class=\"ace_search right\">\\\r\n"+
            "    <button type=\"button\" action=\"hide\" class=\"ace_searchbtn_close\"></button>\\\r\n"+
            "    <div class=\"ace_search_form\">\\\r\n"+
            "        <input class=\"ace_search_field\" placeholder=\"Search for\" spellcheck=\"false\"></input>\\\r\n"+
            "        <button type=\"button\" action=\"findNext\" class=\"ace_searchbtn next\"></button>\\\r\n"+
            "        <button type=\"button\" action=\"findPrev\" class=\"ace_searchbtn prev\"></button>\\\r\n"+
            "    </div>\\\r\n"+
            "    <div class=\"ace_replace_form\">\\\r\n"+
            "        <input class=\"ace_search_field\" placeholder=\"Replace with\" spellcheck=\"false\"></input>\\\r\n"+
            "        <button type=\"button\" action=\"replaceAndFindNext\" class=\"ace_replacebtn\">Replace</button>\\\r\n"+
            "        <button type=\"button\" action=\"replaceAll\" class=\"ace_replacebtn\">All</button>\\\r\n"+
            "    </div>\\\r\n"+
            "    <div class=\"ace_search_options\">\\\r\n"+
            "        <span action=\"toggleRegexpMode\" class=\"ace_button\" title=\"RegExp Search\">.*</span>\\\r\n"+
            "        <span action=\"toggleCaseSensitive\" class=\"ace_button\" title=\"CaseSensitive Search\">Aa</span>\\\r\n"+
            "        <span action=\"toggleWholeWords\" class=\"ace_button\" title=\"Whole Word Search\">\\\\b</span>\\\r\n"+
            "    </div>\\\r\n"+
            "</div>'.replace(/>\\s+/g, \">\");\r\n"+
            "\r\n"+
            "var SearchBox = function(editor, range, showReplaceForm) {\r\n"+
            "    var div = dom.createElement(\"div\");\r\n"+
            "    div.innerHTML = html;\r\n"+
            "    this.element = div.firstChild;\r\n"+
            "\r\n"+
            "    this.$init();\r\n"+
            "    this.setEditor(editor);\r\n"+
            "};\r\n"+
            "\r\n"+
            "(function() {\r\n"+
            "    this.setEditor = function(editor) {\r\n"+
            "        editor.searchBox = this;\r\n"+
            "        editor.container.appendChild(this.element);\r\n"+
            "        this.editor = editor;\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.$initElements = function(sb) {\r\n"+
            "        this.searchBox = sb.querySelector(\".ace_search_form\");\r\n"+
            "        this.replaceBox = sb.querySelector(\".ace_replace_form\");\r\n"+
            "        this.searchOptions = sb.querySelector(\".ace_search_options\");\r\n"+
            "        this.regExpOption = sb.querySelector(\"[action=toggleRegexpMode]\");\r\n"+
            "        this.caseSensitiveOption = sb.querySelector(\"[action=toggleCaseSensitive]\");\r\n"+
            "        this.wholeWordOption = sb.querySelector(\"[action=toggleWholeWords]\");\r\n"+
            "        this.searchInput = this.searchBox.querySelector(\".ace_search_field\");\r\n"+
            "        this.replaceInput = this.replaceBox.querySelector(\".ace_search_field\");\r\n"+
            "    };\r\n"+
            "    \r\n"+
            "    this.$init = function() {\r\n"+
            "        var sb = this.element;\r\n"+
            "        \r\n"+
            "        this.$initElements(sb);\r\n"+
            "        \r\n"+
            "        var _this = this;\r\n"+
            "        event.addListener(sb, \"mousedown\", function(e) {\r\n"+
            "            setTimeout(function(){\r\n"+
            "                _this.activeInput.focus();\r\n"+
            "            }, 0);\r\n"+
            "            event.stopPropagation(e);\r\n"+
            "        });\r\n"+
            "        event.addListener(sb, \"click\", function(e) {\r\n"+
            "            var t = e.target || e.srcElement;\r\n"+
            "            var action = t.getAttribute(\"action\");\r\n"+
            "            if (action && _this[action])\r\n"+
            "                _this[action]();\r\n"+
            "            else if (_this.$searchBarKb.commands[action])\r\n"+
            "                _this.$searchBarKb.commands[action].exec(_this);\r\n"+
            "            event.stopPropagation(e);\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        event.addCommandKeyListener(sb, function(e, hashId, keyCode) {\r\n"+
            "            var keyString = keyUtil.keyCodeToString(keyCode);\r\n"+
            "            var command = _this.$searchBarKb.findKeyCommand(hashId, keyString);\r\n"+
            "            if (command && command.exec) {\r\n"+
            "                command.exec(_this);\r\n"+
            "                event.stopEvent(e);\r\n"+
            "            }\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        this.$onChange = lang.delayedCall(function() {\r\n"+
            "            _this.find(false, false);\r\n"+
            "        });\r\n"+
            "\r\n"+
            "        event.addListener(this.searchInput, \"input\", function() {\r\n"+
            "            _this.$onChange.schedule(20);\r\n"+
            "        });\r\n"+
            "        event.addListener(this.searchInput, \"focus\", function() {\r\n"+
            "            _this.activeInput = _this.searchInput;\r\n"+
            "            _this.searchInput.value && _this.highlight();\r\n"+
            "        });\r\n"+
            "        event.addListener(this.replaceInput, \"focus\", function() {\r\n"+
            "            _this.activeInput = _this.replaceInput;\r\n"+
            "            _this.searchInput.value && _this.highlight();\r\n"+
            "        });\r\n"+
            "    };\r\n"+
            "    this.$closeSearchBarKb = new HashHandler([{\r\n"+
            "        bindKey: \"Esc\",\r\n"+
            "        name: \"");
          out.print(
            "closeSearchBar\",\r\n"+
            "        exec: function(editor) {\r\n"+
            "            editor.searchBox.hide();\r\n"+
            "        }\r\n"+
            "    }]);\r\n"+
            "    this.$searchBarKb = new HashHandler();\r\n"+
            "    this.$searchBarKb.bindKeys({\r\n"+
            "        \"Ctrl-f|Command-f|Ctrl-H|Command-Option-F\": function(sb) {\r\n"+
            "            var isReplace = sb.isReplace = !sb.isReplace;\r\n"+
            "            sb.replaceBox.style.display = isReplace ? \"\" : \"none\";\r\n"+
            "            sb[isReplace ? \"replaceInput\" : \"searchInput\"].focus();\r\n"+
            "        },\r\n"+
            "        \"Ctrl-G|Command-G\": function(sb) {\r\n"+
            "            sb.findNext();\r\n"+
            "        },\r\n"+
            "        \"Ctrl-Shift-G|Command-Shift-G\": function(sb) {\r\n"+
            "            sb.findPrev();\r\n"+
            "        },\r\n"+
            "        \"esc\": function(sb) {\r\n"+
            "            setTimeout(function() { sb.hide();});\r\n"+
            "        },\r\n"+
            "        \"Return\": function(sb) {\r\n"+
            "            if (sb.activeInput == sb.replaceInput)\r\n"+
            "                sb.replace();\r\n"+
            "            sb.findNext();\r\n"+
            "        },\r\n"+
            "        \"Shift-Return\": function(sb) {\r\n"+
            "            if (sb.activeInput == sb.replaceInput)\r\n"+
            "                sb.replace();\r\n"+
            "            sb.findPrev();\r\n"+
            "        },\r\n"+
            "        \"Tab\": function(sb) {\r\n"+
            "            (sb.activeInput == sb.replaceInput ? sb.searchInput : sb.replaceInput).focus();\r\n"+
            "        }\r\n"+
            "    });\r\n"+
            "\r\n"+
            "    this.$searchBarKb.addCommands([{\r\n"+
            "        name: \"toggleRegexpMode\",\r\n"+
            "        bindKey: {win: \"Alt-R|Alt-/\", mac: \"Ctrl-Alt-R|Ctrl-Alt-/\"},\r\n"+
            "        exec: function(sb) {\r\n"+
            "            sb.regExpOption.checked = !sb.regExpOption.checked;\r\n"+
            "            sb.$syncOptions();\r\n"+
            "        }\r\n"+
            "    }, {\r\n"+
            "        name: \"toggleCaseSensitive\",\r\n"+
            "        bindKey: {win: \"Alt-C|Alt-I\", mac: \"Ctrl-Alt-R|Ctrl-Alt-I\"},\r\n"+
            "        exec: function(sb) {\r\n"+
            "            sb.caseSensitiveOption.checked = !sb.caseSensitiveOption.checked;\r\n"+
            "            sb.$syncOptions();\r\n"+
            "        }\r\n"+
            "    }, {\r\n"+
            "        name: \"toggleWholeWords\",\r\n"+
            "        bindKey: {win: \"Alt-B|Alt-W\", mac: \"Ctrl-Alt-B|Ctrl-Alt-W\"},\r\n"+
            "        exec: function(sb) {\r\n"+
            "            sb.wholeWordOption.checked = !sb.wholeWordOption.checked;\r\n"+
            "            sb.$syncOptions();\r\n"+
            "        }\r\n"+
            "    }]);\r\n"+
            "\r\n"+
            "    this.$syncOptions = function() {\r\n"+
            "        dom.setCssClass(this.regExpOption, \"checked\", this.regExpOption.checked);\r\n"+
            "        dom.setCssClass(this.wholeWordOption, \"checked\", this.wholeWordOption.checked);\r\n"+
            "        dom.setCssClass(this.caseSensitiveOption, \"checked\", this.caseSensitiveOption.checked);\r\n"+
            "        this.find(false, false);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.highlight = function(re) {\r\n"+
            "        this.editor.session.highlight(re || this.editor.$search.$options.re);\r\n"+
            "        this.editor.renderer.updateBackMarkers()\r\n"+
            "    };\r\n"+
            "    this.find = function(skipCurrent, backwards) {\r\n"+
            "        var range = this.editor.find(this.searchInput.value, {\r\n"+
            "            skipCurrent: skipCurrent,\r\n"+
            "            backwards: backwards,\r\n"+
            "            wrap: true,\r\n"+
            "            regExp: this.regExpOption.checked,\r\n"+
            "            caseSensitive: this.caseSensitiveOption.checked,\r\n"+
            "            wholeWord: this.wholeWordOption.checked\r\n"+
            "        });\r\n"+
            "        var noMatch = !range && this.searchInput.value;\r\n"+
            "        dom.setCssClass(this.searchBox, \"ace_nomatch\", noMatch);\r\n"+
            "        this.editor._emit(\"findSearchBox\", { match: !noMatch });\r\n"+
            "        this.highlight();\r\n"+
            "    };\r\n"+
            "    this.findNext = function() {\r\n"+
            "        this.find(true, false);\r\n"+
            "    };\r\n"+
            "    this.findPrev = function() {\r\n"+
            "        this.find(true, true);\r\n"+
            "    };\r\n"+
            "    this.replace = function() {\r\n"+
            "        if (!this.editor.getReadOnly())\r\n"+
            "            this.editor.replace(this.replaceInput.value);\r\n"+
            "    };    \r\n"+
            "    this.replaceAndFindNext = function() {\r\n"+
            "        if (!this.editor.getReadOnly()) {\r\n"+
            "            this.editor.replace(this.replaceInput.value);\r\n"+
            "            this.findNext()\r\n"+
            "        }\r\n"+
            "    };\r\n"+
            "    this.replaceAll = function() {\r\n"+
            "        if (!this.editor.getReadOnly())\r\n"+
            "            this.editor.replaceAll(this.replaceInput.value);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "    this.hide = function() {\r\n"+
            "        this.element.style.display = \"none\";\r\n"+
            "        this.editor.keyBinding.removeKeyboardHandler(this.$closeSearchBarKb);\r\n"+
            "        this.editor.focus();\r\n"+
            "    };\r\n"+
            "    this.show = function(value, isReplace) {\r\n"+
            "        this.element.style.display = \"\";\r\n"+
            "        this.replaceBox.style.display = isReplace ? \"\" : \"none\";\r\n"+
            "\r\n"+
            "        this.isReplace = isReplace;\r\n"+
            "\r\n"+
            "        if (value)\r\n"+
            "            this.searchInput.value = value;\r\n"+
            "        this.searchInput.focus();\r\n"+
            "        this.searchInput.select();\r\n"+
            "\r\n"+
            "        this.editor.keyBinding.addKeyboardHandler(this.$closeSearchBarKb);\r\n"+
            "    };\r\n"+
            "\r\n"+
            "}).call(SearchBox.prototype);\r\n"+
            "\r\n"+
            "exports.SearchBox = SearchBox;\r\n"+
            "\r\n"+
            "exports.Search = function(editor, isReplace) {\r\n"+
            "    var sb = editor.searchBox || new SearchBox(editor);\r\n"+
            "    sb.show(editor.session.getTextRange(), isReplace);\r\n"+
            "};\r\n"+
            "\r\n"+
            "});\r\n"+
            ";\r\n"+
            "                (function() {\r\n"+
            "                    ace.require([\"ace/ext/searchbox\"], function() {});\r\n"+
            "                })();\r\n"+
            "            ");

	}
	
}