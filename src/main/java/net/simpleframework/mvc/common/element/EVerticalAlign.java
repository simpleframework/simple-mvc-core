package net.simpleframework.mvc.common.element;

/**
 * Licensed under the Apache License, Version 2.0
 * 
 * @author 陈侃(cknet@126.com, 13910090885)
 *         http://code.google.com/p/simpleframework/
 *         http://www.simpleframework.net
 */
public enum EVerticalAlign {
	inherit,

	baseline,

	sub,

	_super {
		@Override
		public String toString() {
			return "super";
		}
	},

	top,

	middle,

	bottom,

	textTop {
		@Override
		public String toString() {
			return "text-top";
		}
	},

	textBottom {
		@Override
		public String toString() {
			return "text-bottom";
		}
	}
}
