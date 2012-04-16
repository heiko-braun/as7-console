/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */
package org.jboss.as.console.client.shared.subsys.logging;

import org.jboss.ballroom.client.widgets.forms.TextBoxItem;

import com.google.gwt.i18n.client.DateTimeFormat;

/**
 * @author Pavel Slegr
 * @date 03/16/12
 */

public class SuffixValidatingTextItem extends TextBoxItem{

	public SuffixValidatingTextItem() {
		super("suffix", "Suffix");
	}
	
    public SuffixValidatingTextItem(String name, String title) {
        super(name, title);
	}
	@Override
	public boolean validate(String value) {
		boolean validation = true;
		try {
	        final DateTimeFormat format = DateTimeFormat.getFormat(value);
	        final int len = value.length();
	        for (int i = 0; i < len; i ++) {
	            switch (value.charAt(i)) {
	                case 's':
	                case 'S': throw new IllegalArgumentException("Rotating by second or millisecond is not supported");
	            }
	        }
			
		} catch (IllegalArgumentException ex) {
	        this.setErroneous(true);
	        this.setErrMessage(ex.getLocalizedMessage());
	        validation = false;
		}
		return super.validate(value) && validation;
	}
	
}
