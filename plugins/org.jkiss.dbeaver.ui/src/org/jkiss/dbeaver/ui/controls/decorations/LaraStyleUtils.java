/*
 * DBeaver - Universal Database Manager
 * Copyright (C) 2010-2025 DBeaver Corp and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jkiss.dbeaver.ui.controls.decorations;

import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.jkiss.dbeaver.runtime.DBWorkbench;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

public class LaraStyleUtils {
    public static final String PREF_UI_ACTIVATE_LARA_STYLE = "ui.activate.lara.style";

    private static final boolean ENABLED = isEnabled0();

    public static boolean isEnabled() {
        return ENABLED;
    }

    private static boolean isEnabled0() {
        return DBWorkbench.getPlatform().getPreferenceStore().getBoolean(LaraStyleUtils.PREF_UI_ACTIVATE_LARA_STYLE);
    }

    private final Random random = new Random();

    private final List<Font> fontPool = new ArrayList<>();

    private final List<Color> colorPool = new ArrayList<>();

    public void initRandomPool() {
        Display display = Display.getCurrent();
        for (Font f : fontPool) {
            if (f != null && !f.isDisposed()) {
                f.dispose();
            }
        }
        fontPool.clear();
        for (int i = 0; i < 20; i++) {
            fontPool.add(new Font(display, "JetBrains Mono", 12 + random.nextInt(6), SWT.NORMAL));
            fontPool.add(new Font(display, "Fira Code", 12 + random.nextInt(6), SWT.NORMAL));
            fontPool.add(new Font(display, "JetBrains Mono", 12 + random.nextInt(6), SWT.BOLD));
        }

        for (Color c : colorPool) {
            if (c != null && !c.isDisposed()) {
                c.dispose();
            }
        }
        colorPool.clear();
        for (int i = 0; i < 200; i++) {
            colorPool.add(new Color(display,
                    50 + random.nextInt(200),
                    50 + random.nextInt(200),
                    50 + random.nextInt(200)
            ));
        }
    }

    private int findLastWordStart(String content) {
        int lastWordStart = content.length();
        while (lastWordStart > 0 && !Character.isWhitespace(content.charAt(lastWordStart - 1))) {
            lastWordStart--;
        }
        return lastWordStart;
    }

    private StyleRange createRandomStyleRange(int position) {
        StyleRange range = new StyleRange();
        range.start = position;
        range.length = 1;
        range.font = fontPool.get(random.nextInt(fontPool.size()));
        range.foreground = colorPool.get(random.nextInt(colorPool.size()));
        return range;
    }

    private void applyRandomStylesToLastWord(Consumer<StyleRange> consumer) {
        StyledText text = getViewer().getTextWidget();

        int caretOffset = text.getCaretOffset();
        if (caretOffset == 0) return;

        String content = text.getText(0, caretOffset - 1);
        int lastWordStart = findLastWordStart(content);

        for (int i = lastWordStart; i < caretOffset; i++) {
            consumer.accept(createRandomStyleRange(i));
        }
    }

    public void randomLastWord(TextPresentation event) {
        applyRandomStylesToLastWord(event::mergeStyleRange);
    }

    public void randomLastWordAsVerify(VerifyEvent event) {
        StyledText text = getViewer().getTextWidget();
        applyRandomStylesToLastWord(text::setStyleRange);
    }

    SourceViewer viewer;

    public SourceViewer getViewer()
    {
        return viewer;
    }

    public LaraStyleUtils(SourceViewer viewer) {
        this.viewer = viewer;
    }
}
