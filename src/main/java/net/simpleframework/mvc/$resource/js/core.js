/**
 * @author 陈侃(cknet@126.com, 13910090885) https://github.com/simpleframework
 *         http://www.simpleframework.net
 */
var Browser = {
  support : {
    canvas : !!document.createElement("canvas").getContext
  }
};

var $call = function(o) {
  try {
    if (typeof o === "function") {
      return o.apply(o, Array.prototype.slice.call(arguments, 1));
    } else if (typeof o === "string") {
      return eval(o);
    }
  } catch (e) {
    alert(e.name + ": " + e.message);
  }
};

/** 核心对象 */
var $Actions = {
  loc : function(url, open) {
    if (typeof url != "string" || url.length == 0)
      return;
    if (url.charAt(0) == "/"
        && url.toUpperCase().lastIndexOf(CONTEXT_PATH.toUpperCase(), 0) != 0) {
      url = CONTEXT_PATH + url;
    }
    if (open)
      window.open(url);
    else
      window.location = url;
  },

  reloc : function() {
    window.location.reload();
  },

  callSafely : function(act, parameters, callback) {
    if (!act)
      return;
    var action = typeof act == "string" ? $Actions[act] : act;
    var exec = function() {
      if (callback && callback(action))
        return;
      action(parameters);
    };
    if (action) {
      setTimeout(exec, 10);
    } else {
      var c = 0;
      var sh = setInterval(function() {
        if (action)
          exec();
        else if (c++ < 10)
          return;
        clearInterval(sh);
      }, 500);
    }
  }
};

/** UI对象 */
var $UI = {
  head : document.getElementsByTagName("head")[0],

  setBrowserTitle : function(str, append) {
    document.title = append ? document.title + " - " + str : str;
  },

  addCSS : function(css) {
    var style = document.createElement("style");
    style.setAttribute("type", "text/css");
    style.setAttribute("media", "screen");

    this.head.appendChild(style);
    if (style.styleSheet)
      style.styleSheet.cssText = css;
    else
      style.appendText(css);
    return style;
  },

  _toUrl : function(url) {
    var p = url.indexOf("?");
    return ((p < 0) ? url : url.substring(0, p)).toLowerCase();
  },

  addStylesheet : function(stylesheets) {
    if (!stylesheets)
      return;

    for (var i = 0; i < stylesheets.length; i++) {
      var stylesheet = stylesheets[i].makeElement();

      var href = stylesheet.getAttribute("href");
      if (!href)
        continue;
      href = $UI._toUrl(href);

      var update = false;
      var links = document.getElementsByTagName("link") || [];
      for (var j = links.length - 1; j >= 0; j--) {
        var _href = links[j].getAttribute("href");
        if (_href && $UI._toUrl(_href) == href) {
          update = true;
          break;
        }
      }
      if (!update) {
        this.head.appendChild(stylesheet);
      }
    }
  },

  _getScriptById : function(id) {
    if (!id)
      return;
    var scripts = document.getElementsByTagName("script");
    for (var i = scripts.length - 1; i >= 0; i--) {
      var _id = scripts[i].getAttribute("id");
      if (_id && _id == id)
        return scripts[i];
    }
  },

  addScript : function(scripts, scriptText, id) {
    var doScriptText = function(code, id) {
      if (!code || code.trim().length == 0)
        return;

      var script = $UI._getScriptById(id);
      if (script)
        script.parentNode.removeChild(script);

      script = $UI.createScriptElement();
      if (id)
        script.id = id;
      script.text = code;
    };

    if (scripts.length == 0) {
      doScriptText(scriptText, id);
    } else {
      var script = scripts.removeAt(0);
      var src = script.getAttribute("src");
      if (src) {
        src = $UI._toUrl(src);
        var _script = null;
        var _scripts = document.getElementsByTagName("script");
        for (var i = _scripts.length - 1; i >= 0; i--) {
          var _src = _scripts[i].getAttribute("src");
          if (_src && $UI._toUrl(_src) == src) {
            _script = _scripts[i];
            break;
          }
        }
        if (!_script) {
          _script = $UI.createScriptElement();
          _script.done = "doing";
          if (_script.readyState
              && (!document.documentMode || document.documentMode < 10)) {
            _script.onreadystatechange = function() {
              var s = _script.readyState;
              if (s == "loaded" /* || s == "complete" */) {
                _script.onreadystatechange = null;
                _script.done = "done";
                $UI.addScript(scripts, scriptText, id);
              }
            };
          } else {
            _script.onload = function() {
              _script.done = "done";
              $UI.addScript(scripts, scriptText, id);
            }
          }
          _script.src = src;
        } else {
          if (!_script.done || _script.done == "done") {
            $UI.addScript(scripts, scriptText, id);
          } else {
            var c = 0;
            var sh = setInterval(function() {
              if (_script.done == "done" || c++ > 50) {
                $UI.addScript(scripts, scriptText, id);
                clearInterval(sh);
              }
            }, 100);
          }
        }
      } else {
        doScriptText(script.text, script.id);
        $UI.addScript(scripts, scriptText, id);
      }
    }
  },

  createScriptElement : function() {
    var script = document.createElement("script");
    script.type = "text/javascript";
    this.head.appendChild(script);
    return script;
  },

  moveCursorToEnd : function(el) {
    if (typeof el.selectionStart == "number") {
      el.selectionStart = el.selectionEnd = el.value.length;
    } else if (typeof el.createTextRange != "undefined") {
      el.focus();
      var range = el.createTextRange();
      range.collapse(false);
      range.select();
    }
  },

  evalParam : function(p) {
    if (p && p.toLowerCase().startsWith("javascript:"))
      p = eval(p.substring(11));
    return p;
  }
};

/** 效果对象 */
var $Effect = {};

(function() {
  /** document */
  document.getEvent = function(e) {
    var ev = e || window.event;
    if (!ev) {
      var c = document.getEvent.caller;
      var arr = [];
      while (c && c.arguments) {
        ev = c.arguments[0];
        if (ev
            && ev.constructor
            && (window.Event == ev.constructor || window.MouseEvent == ev.constructor)) {
          break;
        } else {
          ev = undefined;
        }
        if (c == c.caller || arr.include(c.caller))
          break;
        else
          arr.push(c = c.caller);
      }
    }
    return ev && Event.extend(ev).target ? ev : null;
  };

  document.setCookie = function(key, value, hour) {
    var expires = "";
    if (hour) {
      var date = new Date();
      date.setTime(date.getTime() + (hour * 60 * 60 * 1000));
      expires = "; expires=" + date.toGMTString();
    }
    document.cookie = key + "=" + encodeURIComponent(value) + expires
        + "; path=/";
  };

  document.removeCookie = function(key) {
    document.setCookie(key, "", -1);
  };

  document.getCookie = function(key) {
    var cookies = document.cookie.match(key + '=(.*?)(;|$)');
    if (cookies) {
      return decodeURI(cookies[1]);
    } else {
      return null;
    }
  };

  if (!String.prototype.trim) {
    String.prototype.trim = function() {
      return this.replace(/^\s+/, '').replace(/\s+$/, '');
    };
  }

  var StylesheetFragment = new RegExp("<link[^>]+stylesheet[^>]*>", "img");

  String.prototype.stripStylesheets = function() {
    return this.replace(StylesheetFragment, '');
  };

  String.prototype.toStylesheets = function() {
    return this.match(StylesheetFragment) || [];
  }

  var ScriptFragment = new RegExp("<script[^>]*>([\\S\\s]*?)<\/script\\s*>",
      "img");

  String.prototype.stripScripts = function() {
    return this.replace(ScriptFragment, '');
  };

  String.prototype.toScripts = function() {
    return this.match(ScriptFragment) || [];
  };

  String.prototype.convertHtmlLines = function() {
    return this.replace(/\r/g, '<br>').replace(/\n/g, '<br>');
  };

  var hex_chr = "0123456789abcdef";

  String.prototype.md5 = function(str) {
    /*
     * Convert a 32-bit number to a hex string with ls-byte first
     */
    function rhex(num) {
      str = "";
      for (j = 0; j <= 3; j++)
        str += hex_chr.charAt((num >> (j * 8 + 4)) & 0x0F)
            + hex_chr.charAt((num >> (j * 8)) & 0x0F);
      return str;
    }
    /*
     * Convert a string to a sequence of 16-word blocks, stored as an array.
     * Append padding bits and the length, as described in the MD5 standard.
     */
    function str2blks_MD5(str) {
      nblk = ((str.length + 8) >> 6) + 1;
      blks = new Array(nblk * 16);
      for (i = 0; i < nblk * 16; i++)
        blks[i] = 0;
      for (i = 0; i < str.length; i++)
        blks[i >> 2] |= str.charCodeAt(i) << ((i % 4) * 8);
      blks[i >> 2] |= 0x80 << ((i % 4) * 8);
      blks[nblk * 16 - 2] = str.length * 8;
      return blks;
    }
    /*
     * Add integers, wrapping at 2^32. This uses 16-bit operations internally to
     * work around bugs in some JS interpreters.
     */
    function add(x, y) {
      var lsw = (x & 0xFFFF) + (y & 0xFFFF);
      var msw = (x >> 16) + (y >> 16) + (lsw >> 16);
      return (msw << 16) | (lsw & 0xFFFF);
    }
    /*
     * Bitwise rotate a 32-bit number to the left
     */
    function rol(num, cnt) {
      return (num << cnt) | (num >>> (32 - cnt));
    }
    /*
     * These functions implement the basic operation for each round of the
     * algorithm.
     */
    function cmn(q, a, b, x, s, t) {
      return add(rol(add(add(a, q), add(x, t)), s), b);
    }
    function ff(a, b, c, d, x, s, t) {
      return cmn((b & c) | ((~b) & d), a, b, x, s, t);
    }
    function gg(a, b, c, d, x, s, t) {
      return cmn((b & d) | (c & (~d)), a, b, x, s, t);
    }
    function hh(a, b, c, d, x, s, t) {
      return cmn(b ^ c ^ d, a, b, x, s, t);
    }
    function ii(a, b, c, d, x, s, t) {
      return cmn(c ^ (b | (~d)), a, b, x, s, t);
    }

    x = str2blks_MD5(this.toString());
    a = 1732584193;
    b = -271733879;
    c = -1732584194;
    d = 271733878;

    for (i = 0; i < x.length; i += 16) {
      olda = a;
      oldb = b;
      oldc = c;
      oldd = d;

      a = ff(a, b, c, d, x[i + 0], 7, -680876936);
      d = ff(d, a, b, c, x[i + 1], 12, -389564586);
      c = ff(c, d, a, b, x[i + 2], 17, 606105819);
      b = ff(b, c, d, a, x[i + 3], 22, -1044525330);
      a = ff(a, b, c, d, x[i + 4], 7, -176418897);
      d = ff(d, a, b, c, x[i + 5], 12, 1200080426);
      c = ff(c, d, a, b, x[i + 6], 17, -1473231341);
      b = ff(b, c, d, a, x[i + 7], 22, -45705983);
      a = ff(a, b, c, d, x[i + 8], 7, 1770035416);
      d = ff(d, a, b, c, x[i + 9], 12, -1958414417);
      c = ff(c, d, a, b, x[i + 10], 17, -42063);
      b = ff(b, c, d, a, x[i + 11], 22, -1990404162);
      a = ff(a, b, c, d, x[i + 12], 7, 1804603682);
      d = ff(d, a, b, c, x[i + 13], 12, -40341101);
      c = ff(c, d, a, b, x[i + 14], 17, -1502002290);
      b = ff(b, c, d, a, x[i + 15], 22, 1236535329);

      a = gg(a, b, c, d, x[i + 1], 5, -165796510);
      d = gg(d, a, b, c, x[i + 6], 9, -1069501632);
      c = gg(c, d, a, b, x[i + 11], 14, 643717713);
      b = gg(b, c, d, a, x[i + 0], 20, -373897302);
      a = gg(a, b, c, d, x[i + 5], 5, -701558691);
      d = gg(d, a, b, c, x[i + 10], 9, 38016083);
      c = gg(c, d, a, b, x[i + 15], 14, -660478335);
      b = gg(b, c, d, a, x[i + 4], 20, -405537848);
      a = gg(a, b, c, d, x[i + 9], 5, 568446438);
      d = gg(d, a, b, c, x[i + 14], 9, -1019803690);
      c = gg(c, d, a, b, x[i + 3], 14, -187363961);
      b = gg(b, c, d, a, x[i + 8], 20, 1163531501);
      a = gg(a, b, c, d, x[i + 13], 5, -1444681467);
      d = gg(d, a, b, c, x[i + 2], 9, -51403784);
      c = gg(c, d, a, b, x[i + 7], 14, 1735328473);
      b = gg(b, c, d, a, x[i + 12], 20, -1926607734);

      a = hh(a, b, c, d, x[i + 5], 4, -378558);
      d = hh(d, a, b, c, x[i + 8], 11, -2022574463);
      c = hh(c, d, a, b, x[i + 11], 16, 1839030562);
      b = hh(b, c, d, a, x[i + 14], 23, -35309556);
      a = hh(a, b, c, d, x[i + 1], 4, -1530992060);
      d = hh(d, a, b, c, x[i + 4], 11, 1272893353);
      c = hh(c, d, a, b, x[i + 7], 16, -155497632);
      b = hh(b, c, d, a, x[i + 10], 23, -1094730640);
      a = hh(a, b, c, d, x[i + 13], 4, 681279174);
      d = hh(d, a, b, c, x[i + 0], 11, -358537222);
      c = hh(c, d, a, b, x[i + 3], 16, -722521979);
      b = hh(b, c, d, a, x[i + 6], 23, 76029189);
      a = hh(a, b, c, d, x[i + 9], 4, -640364487);
      d = hh(d, a, b, c, x[i + 12], 11, -421815835);
      c = hh(c, d, a, b, x[i + 15], 16, 530742520);
      b = hh(b, c, d, a, x[i + 2], 23, -995338651);

      a = ii(a, b, c, d, x[i + 0], 6, -198630844);
      d = ii(d, a, b, c, x[i + 7], 10, 1126891415);
      c = ii(c, d, a, b, x[i + 14], 15, -1416354905);
      b = ii(b, c, d, a, x[i + 5], 21, -57434055);
      a = ii(a, b, c, d, x[i + 12], 6, 1700485571);
      d = ii(d, a, b, c, x[i + 3], 10, -1894986606);
      c = ii(c, d, a, b, x[i + 10], 15, -1051523);
      b = ii(b, c, d, a, x[i + 1], 21, -2054922799);
      a = ii(a, b, c, d, x[i + 8], 6, 1873313359);
      d = ii(d, a, b, c, x[i + 15], 10, -30611744);
      c = ii(c, d, a, b, x[i + 6], 15, -1560198380);
      b = ii(b, c, d, a, x[i + 13], 21, 1309151649);
      a = ii(a, b, c, d, x[i + 4], 6, -145523070);
      d = ii(d, a, b, c, x[i + 11], 10, -1120210379);
      c = ii(c, d, a, b, x[i + 2], 15, 718787259);
      b = ii(b, c, d, a, x[i + 9], 21, -343485551);

      a = add(a, olda);
      b = add(b, oldb);
      c = add(c, oldc);
      d = add(d, oldd);
    }
    return rhex(a) + rhex(b) + rhex(c) + rhex(d);
  };

  String.prototype.addParameter = function(parameters) {
    if (!parameters || parameters == '') {
      return this.toString();
    }
    var p = this.indexOf('?');
    var request;
    var query;
    if (p > -1) {
      request = this.substring(0, p);
      query = this.substring(p + 1);
    } else {
      var isQueryString = this.indexOf('=') > 0;
      if (isQueryString) {
        request = '';
        query = this.toString();
      } else {
        request = this.toString();
        query = '';
      }
    }
    var o = new Object();
    var doAttri = function(q) {
      var o2;
      if (typeof q == "string") {
        o2 = new Object();
        var qArr = q.length > 0 ? q.split("&") : "";
        for (var i = 0; i < qArr.length; i++) {
          var vArr = qArr[i].split("=");
          var k = vArr[0];
          if (!o2[k])
            o2[k] = new Array();
          o2[k].push(vArr.length > 1 ? vArr[1] : "");
        }
      } else {
        o2 = q;
      }
      for (property in o2) {
        o[property] = o2[property];
      }
    }
    doAttri(query);
    doAttri(parameters);
    var ret = "";
    for (property in o) {
      var v = o[property];
      for (var j = 0; j < v.length; j++) {
        ret += ("&" + property + "=" + v[j]);
      }
    }
    if (ret.length > 0)
      ret = ret.substring(1);
    if (request.length > 0)
      request += '?';
    return request + ret;
  };

  String.prototype.makeElement = function() {
    var c = this.toString();
    var wrapper = document.createElement('div');
    wrapper.innerHTML = c;
    return wrapper.firstElementChild || wrapper.firstChild
        || document.createElement(c);
  };

  Number.prototype.toFileString = function() {
    var size = this;
    if (size < 0) {
      return "";
    } else {
      var str;
      if (size > 1024 * 1024) {
        str = (Math.round((size / (1024 * 1024)) * 100) / 100) + "MB";
      } else if (size > 1024) {
        str = (Math.round((size / 1024) * 100) / 100) + "KB";
      } else {
        str = size + "B";
      }
      return str;
    }
  };

  // ---- Array
  Array.prototype.empty = function() {
    return !this.length;
  };

  Array.prototype.removeAt = function(index) {
    var object = this[index];
    this.splice(index, 1);
    return object;
  };

  Array.prototype.remove = function(object) {
    var index;
    while ((index = this.indexOf(object)) != -1)
      this.removeAt(index);
    return object;
  };

  Array.prototype.insert = function(index) {
    var args = this.slice.call(arguments);
    args.shift();
    this.splice.apply(this, [ index, 0 ].concat(args));
    return this;
  };
})();
