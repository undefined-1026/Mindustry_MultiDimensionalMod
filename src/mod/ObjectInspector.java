package mod;

import arc.Core;
import arc.audio.Sound;
import arc.func.Cons2;
import arc.graphics.*;
import arc.input.KeyCode;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.*;
import arc.scene.event.*;
import arc.scene.ui.*;
import arc.scene.ui.layout.*;
import arc.struct.*;

import arc.util.*;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.Effect;
import mindustry.gen.*;
import mindustry.graphics.*;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.ui.*;
import rhino.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.Stack;

import static mindustry.Vars.ui;

public class ObjectInspector extends Table {
    private static final float panelWidth = 420f;
    private static final float panelHeight = 520f;
    private static final int maxDepth = 8;
    private final ReflectUtil r = new ReflectUtil();

    public int Object = -1;
    public int changes = 0;
    public Stack<ObjectPage> pages=  new Stack<>();
    private Object target;
    private TextField searchField;
    private Table contentTable;
    private Seq<ClassNode> allNodes = new Seq<>();
    private Object selectedValue;
    private Field selectedField;
    private Object selectedTarget;
    private String searchQuery = "";
    private ScrollPane scrollPane;
    public boolean updateList = false;

    // 新增：数组编辑状态
    private Object editingArray = null;
    private int editingArrayIndex = -1;
    private Class<?> editingArrayComponentType = null;

    private long lastUpdate;
    private static final long updateInterval = 500; // FIX: 增加到500ms，减少刷新频率

    public ObjectInspector(){
        super(Styles.black6);
        setupUI();
    }

    private void setupUI(){
        // 标题栏 + 搜索 + 关闭

        table(Styles.black8,top->{
            top.table(Styles.black5,drag->{
                drag.name = "drag";
                drag.image(Core.atlas.find("fieldmodifier-ui-drag")).size(25);
                drag.addListener(new InputListener(){
                    float dx, dy;
                    @Override
                    public void touchDragged(InputEvent event, float mx, float my, int pointer){
                        if(Core.app.isMobile() && pointer != 0) return;
                        ObjectInspector.this.x = Core.input.mouseX()-dx;
                        ObjectInspector.this.y = Core.input.mouseY()-dy;
                    }

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                        if(Core.app.isMobile() && pointer != 0) return false;

                        dx = Core.input.mouseX() - ObjectInspector.this.x;
                        dy = Core.input.mouseY() - ObjectInspector.this.y;
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                        if(Core.app.isMobile() && pointer != 0) return;
                        ObjectInspector self = ObjectInspector.this;
                        self.x = Mathf.clamp(self.x,0,Core.graphics.getWidth()-self.width);
                        self.y = Mathf.clamp(self.y,0,Core.graphics.getHeight()-self.height);
                    }
                });

            }).left().pad(5f);
            top.add("字段修改器").color(Pal.accent).expandX().center().pad(5f);
            top.table(Styles.black5,scl->{
                scl.name = "scale";
                scl.image(Core.atlas.find("fieldmodifier-ui-scale")).size(25);
                scl.addListener(new InputListener(){
                    float cx,cy,ew,eh;
                    @Override
                    public void touchDragged(InputEvent event, float a, float b, int pointer){
                        if(Core.app.isMobile() && pointer != 0) return;
                        float mx = (Core.input.mouseX()-cx);
                        float my = (Core.input.mouseY()-cy);
                        ObjectInspector.this.setSize(ew+mx,eh+my);
                    }

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, KeyCode button){
                        if(Core.app.isMobile() && pointer != 0) return false;
                        cx = Core.input.mouseX();
                        cy = Core.input.mouseY();
                        ew = ObjectInspector.this.width;
                        eh = ObjectInspector.this.height;
                        return true;
                    }

                    @Override
                    public void touchUp(InputEvent event, float x, float y, int pointer, KeyCode button) {
                        if(Core.app.isMobile() && pointer != 0) return;
                        ObjectInspector self = ObjectInspector.this;
                        self.x = Mathf.clamp(self.x,0,Core.graphics.getWidth()-self.width);
                        self.y = Mathf.clamp(self.y,0,Core.graphics.getHeight()-self.height);
                    }
                });
            }).right().pad(5f);
        }).growX().row();

        table(t -> {

            t.left();

            var b = new ImageButton(Icon.refresh,Styles.clearTogglei);
            b.clicked(()->{
                updateList = b.isChecked();
            });
            b.resizeImage(Icon.refresh.imageSize());
            t.add(b).size(28).right().pad(4);
            searchField = t.field("", s -> {
                searchQuery = s.toLowerCase();
                refresh();
                //refreshFieldVisibility();
            }).growX().get();
            searchField.setMessageText("搜索字段...");
            t.button(Icon.leftOpen,Styles.cleari,this::back).size(28).right().pad(4);
            t.button(Icon.rotate,Styles.cleari, this::refresh).size(28).right().pad(4);
            t.button(Icon.cancel, Styles.cleari, this::close).size(28).right().pad(4);

        }).growX().row();
        // 目标信息
        table(info -> {
            info.name = "target-info";
        }).growX().row();

        // 字段列表（可滚动）
        pane( p -> {
            contentTable = p;
            contentTable.top().left();
        }).grow().scrollX(false).row();
        scrollPane = (ScrollPane) contentTable.parent;
        scrollPane.setScrollY(0);

        // 底部编辑区
        var editCell = table(edit -> {
            edit.name = "edit-area";
            //edit.visible(() -> selectedField != null);
        }).growX().bottom().pad(4f);


        // FIX: 不用 fillParent，手动设置大小和位置，避免布局冲突
        setSize(panelWidth, panelHeight);

    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(Math.max(350,width), Math.max(350,height));
    }

    // FIX: 重写 act 而不是用 update() lambda，避免布局循环
    Interval timer = new Interval();
    @Override
    public void act(float delta){
        super.act(delta);


        // 检查目标建筑是否还存在
        if(target != null && target instanceof Healthc h&& !h.isValid()){
            // 建筑被拆了，关闭面板
            close();
            return;
        }

        if(!Vars.state.isGame()){
            close();
            return;
        }

        if(updateList && timer.get(0,10f)){
            refresh();
        }
    }
    //is null
    public void inspect(Object target){
        if(target == null) return;
        this.target = target;
        selectedField = null;
        selectedTarget = null;
        searchField.setText("");
        searchQuery = "";
        if (!visible) {
            setSize(panelWidth, panelHeight);
            setPosition(40,40);
        }
        refresh();
        setVisible(true);
        toFront();

    }


    // FIX: 统一的关闭方法
    public void close(){
        setVisible(false);
        target = null;
        selectedField = null;
        selectedTarget = null;
        searchQuery = "";
        pages.clear();
    }

    public void back(){
        if(pages.isEmpty())return;
        var backPage = pages.pop();

        if(backPage == null || backPage.target == null) return;
        this.target = backPage.target;
        selectedField = null;
        selectedTarget = null;
        searchField.setText("");
        searchQuery = "";
        refresh(backPage.opens,backPage.paneY);
        setVisible(true);
        toFront();
    }
    public void enter(Object target){
        boolean[] opens = new boolean[allNodes.size];
        for(int i=0;i<allNodes.size;i++){
            if(allNodes.get(i) == null)continue;
            opens[i] = !allNodes.get(i).collapser.isCollapsed();
        }
        pages.add(new ObjectPage(this.target,this.scrollPane.getScrollY(),opens));
        inspect(target);
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (!visible) {
            target = null;
            selectedField = null;
            selectedTarget = null;
        }
    }

    private void refresh(){
        boolean[] lastOper = new boolean[allNodes.size];
        for(int i=0;i<allNodes.size;i++){
            if(allNodes.get(i) == null)continue;
            lastOper[i] = !allNodes.get(i).collapser.isCollapsed();
        }
        float lastY = 0;
        if(scrollPane!=null){
            lastY = scrollPane.getScrollY();
        }

        refresh(lastOper,lastY);
    }

    private void refresh(@Nullable boolean[] open,float paneY){
        if(target != null && target instanceof Healthc h&& !h.isValid()) return;


        lastUpdate = Time.millis();

        // 更新目标信息
        Table info = find("target-info");
        boolean isArray = false;
        if(info != null){
            info.clear();
            if(target !=null && target.getClass().isArray()){
                Class<?> compType = target.getClass().getComponentType();
                int length = Array.getLength(target);
                info.add(compType.getSimpleName()+"["+length+"]").color(Pal.accent).pad(4).row();
                isArray = true;
            }
            if(target instanceof Building b){
                info.add(b.block.emoji() + " " + b.block.name)
                        .color(Pal.accent).pad(4f);
                info.add(" @ " + b.tileX() + "," + b.tileY())
                        .color(Color.gray).pad(4f);
                info.row();
            }else if(target instanceof Unit u){
                info.add(u.type.emoji() + " " + u.type.name)
                        .color(Pal.accent).pad(4f);
                info.add(" @ " + Strings.fixed(u.x(),2) + "," + Strings.fixed(u.y(),2))
                        .color(Color.gray).pad(4f);
                info.row();
            }
            info.add("Class: " + target.getClass().getSimpleName())
                    .color(Color.lightGray).fontScale(0.8f).pad(4f);
        }
//        boolean hasSave = false;
//        boolean[] lastOper = new boolean[allNodes.size];
//        for(int i=0;i<allNodes.size;i++){
//            if(allNodes.get(i) == null)continue;
//            hasSave = true;
//            lastOper[i] = !allNodes.get(i).collapser.isCollapsed();
//        }
//        float lastY = 0;
//        if(scrollPane!=null){
//            lastY = scrollPane.getScrollY();
//        }
        allNodes.clear();
        contentTable.clear();
        if(isArray){
            buildArrayList(contentTable,target);
            if(scrollPane != null){
                scrollPane.setScrollY(paneY);
            }
            refreshFieldVisibility();
            buildEditArea();
            return;
        }

        Class<?> clazz = target.getClass();
        changes++;
        int depth = 0;
        while(clazz != null && depth < maxDepth){
            ClassNode node = new ClassNode(clazz, target);
            allNodes.add(node);

            boolean isFirst = open == null || open.length == 0?depth == 0:open[Mathf.clamp(depth,0,open.length-1)];
            boolean startCollapsed = !isFirst;

            Class<?> finalClazz = clazz;
            int finalDepth = depth;

            // FIX: 标题行 - 用单独的变量保存 Collapser 引用
            final Collapser[] collapserRef = new Collapser[1];

            // FIX: 创建 Collapser 并保存引用
            Collapser collapser;
            collapser = new Collapser(inner -> {
                buildFieldList(inner, node.fields, target);
            }, startCollapsed);
            collapserRef[0] = collapser;
            node.collapser = collapser;
            contentTable.add(collapser).growX().row();



            contentTable.table(header -> {
                header.left();
                header.defaults().pad(2f);

                // FIX: 折叠按钮 - 使用 TextButton，clicked 在创建后再设置
                ImageButton toggleBtn = header.button(
                        Icon.layers,
                        () -> {
                            if(collapserRef[0] != null){
                                collapserRef[0].toggle();
                            }
                        }
                ).size(24f).get();

                // FIX: 按钮点击事件在获取 collapserRef 后设置
                toggleBtn.clicked(() -> {
                    if(collapserRef[0] != null){
                        collapserRef[0].toggle();
                    }
                });

                String className = finalClazz.getSimpleName();
                if(className.isEmpty()) className = finalClazz.getName();
                header.add(className).color(finalDepth == 0 ? Pal.accent : Color.lightGray).padLeft(4f);
                header.add(" (" + node.fields.size + ")").color(Color.gray).fontScale(0.8f);
                header.add().growX();

                // FIX: 整个标题行点击也能 toggle
                header.clicked(() -> {
                    if(collapserRef[0] != null){
                        collapserRef[0].toggle();
                    }
                });
                header.background(Tex.underline);


            }).growX().pad(2f).row();



            clazz = clazz.getSuperclass();
            depth++;
        }

        scrollPane.setScrollY(paneY);

        refreshFieldVisibility();
        buildEditArea();
    }

    private void buildFieldList(Table table, Seq<Field> fields, Object instance){
        if(instance!=null && instance.getClass().isArray()){
            buildArrayList(table,instance);
            return;
        }
        for(Field f : fields){
            if(!searchQuery.isEmpty() && !f.getName().toLowerCase().contains(searchQuery))continue;
            f.setAccessible(true);
            Object value;
            try {
                value = f.get(instance);
            } catch(Exception e){
                value = "[ERROR]";
            }

            final Object finalValue = value;
            final Field finalField = f;

            table.table(row -> {
                row.name = "field-" + f.getName();
                row.left();
                row.defaults().pad(2f);
                Class<?> type = f.getType();
                String typeName = simplifyType(f.getType());
                boolean canOpen = !isSimplifyType(type) && !typeName.equals("this$0");
                row.add(typeName).color(canOpen?Pal.placing:Color.gray).fontScale(0.75f).width(70f).padRight(4f);

                String mods = "";
                if(Modifier.isPrivate(f.getModifiers())) mods += "p";
                if(Modifier.isProtected(f.getModifiers())) mods += "P";
                if(Modifier.isFinal(f.getModifiers())) mods += "F";
                if(!mods.isEmpty()){
                    row.add("[" + mods + "]").color(Color.darkGray).fontScale(0.7f).padRight(4f);
                }

                row.add(f.getName()).color(Color.white).padRight(6f);
                row.add().growX();

                String valStr = !f.getName().equals("code") ?insertEvery(formatValue(finalValue),16,"\n"):formatValue(finalValue);
                Label valLabel = row.add(valStr).color(valueColor(finalValue)).padRight(4f).get();
                valLabel.setFontScale(0.85f);
                valLabel.setWrap(false);

                // FIX: 点击选中字段
                row.clicked(() -> {
                    selectedField = finalField;
                    selectedValue = finalValue;
                    selectedTarget = target;

                    editingArray = null;
                    editingArrayIndex = -1;
                    editingArrayComponentType = null;
                    buildEditArea();
                });
                if(canOpen){
                    row.button(Icon.resize,()->{
                        java.lang.Object newTarget = null;
                        try {
                            newTarget = f.get(target);
                        } catch (IllegalAccessException e) {
                            Log.err("get field fail", e.getMessage());
                        }
                        if(newTarget!=null)enter(newTarget);
                    }).size(32);
                }

                row.hovered(() -> row.background(Styles.flatDown));
                row.exited(() -> row.background(null));
                row.touchable = Touchable.enabled;
            }).growX().pad(1f).row();
        }
    }

    private void buildArrayList(Table table, Object array){
        int length = java.lang.reflect.Array.getLength(array);
        Class<?> compType = array.getClass().getComponentType();

        for(int i = 0; i < length; i++){
            final int index = i;
            Object value = java.lang.reflect.Array.get(array, index);

            table.table(row -> {
                row.name = "field-[" + index + "]";  // 用 field- 前缀让搜索能匹配到
                row.left();
                row.defaults().pad(2f);

                String typeName = simplifyType(compType);
                boolean isPrimitive = compType.isPrimitive();
                boolean canOpen = !isSimplifyType(compType) && !isPrimitive && value != null;

                // 类型列
                row.add(typeName).color(canOpen ? Pal.placing : Color.gray).fontScale(0.75f).width(70f).padRight(4f);

                // "字段名"就是索引
                row.add("[" + index + "]").color(Color.gray).padRight(6f);
                row.add().growX();

                // 值
                String valStr = insertEvery(formatValue(value),16,"\n");
                Label valLabel = row.add(valStr).color(valueColor(value)).padRight(4f).get();
                valLabel.setFontScale(0.85f);
                valLabel.setWrap(false);

                // 点击选中 - 设置数组编辑状态
                row.clicked(() -> {
                    selectedField = null;
                    selectedTarget = null;
                    selectedValue = null;

                    // 设置数组元素编辑状态
                    editingArray = array;
                    editingArrayIndex = index;
                    editingArrayComponentType = compType;
                    buildEditArea();
                });

                // 进入按钮（如果元素是对象）
                if(canOpen){
                    row.button(Icon.resize, () -> enter(value)).size(32);
                }

                row.hovered(() -> row.background(Styles.flatDown));
                row.exited(() -> row.background(null));
                row.touchable = Touchable.enabled;
            }).growX().pad(1f).row();
        }
    }

    public String insertEvery(String text,int interval ,String insert){
        if(text == null || interval<=0 || insert == null)return text;
        if(text.length()<=interval)return text;

        StringBuffer sb = new StringBuffer();
        for(int i=0;i<text.length();i++){
            sb.append(text.charAt(i));
            if((i+1)%interval == 0 && i!= text.length()-1){
                sb.append(insert);
            }
        }
        return sb.toString();
    }
    // FIX: 搜索过滤
    private void refreshFieldVisibility(){
        if(contentTable == null || searchQuery.isEmpty()){
            // 搜索为空时显示所有
            setAllFieldRowsVisible(true);
            return;
        }

        // 遍历所有字段行
        forEachFieldRow((row, fieldName) -> {
            boolean match = fieldName.toLowerCase().contains(searchQuery);
            row.visible = (match);
        });
    }

    private void setAllFieldRowsVisible(boolean visible){
        forEachFieldRow((row, fieldName) -> row.visible = (visible));
    }

    private void forEachFieldRow(Cons2<Table, String> cons){
        if(contentTable == null) return;

        for(Element child : contentTable.getChildren()){
            // 直接子元素可能是 Table（标题行）或 Collapser
            if(child instanceof Collapser){
                Collapser collapser = (Collapser) child;
                for(Element inner : collapser.getChildren()){
                    if(inner instanceof Table){
                        Table innerTable = (Table) inner;
                        for(Element row : innerTable.getChildren()){
                            if(row instanceof Table){
                                Table fieldRow = (Table) row;
                                String name = fieldRow.name;
                                if(name != null && name.startsWith("field-")){
                                    cons.get(fieldRow, name.substring(6));
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    private void buildEditArea(){
        Table edit = find("edit-area");
        if(edit == null) return;
        edit.clear();

        // FIX: 检查是否有选中内容（普通字段或数组元素）
        boolean hasSelection = (selectedField != null && selectedTarget != null)
                || editingArray != null;

        if(!hasSelection){
            edit.visible = (false);
            return;
        }

        edit.visible = (true);

        edit.table(t -> {
            t.left();
            t.add("选中: ").color(Color.gray).fontScale(0.8f);

            // FIX: 统一获取名称、当前值、类型
            String name;
            Object current;
            Class<?> type;

            if(editingArray != null){
                // 数组元素模式
                name = "[" + editingArrayIndex + "]";
                current = java.lang.reflect.Array.get(editingArray, editingArrayIndex);
                type = editingArrayComponentType;
            } else {
                // 普通字段模式
                name = selectedField.getName();
                try {
                    current = selectedField.get(selectedTarget);
                } catch(Exception e){
                    current = null;
                }
                type = selectedField.getType();
            }

            t.add(name).color(Pal.accent);
            t.add().growX();

            // 关闭按钮
            t.button(Icon.cancel, Styles.cleari, () -> {
                selectedField = null;
                selectedTarget = null;
                editingArray = null;      // FIX: 清空数组状态
                editingArrayIndex = -1;
                editingArrayComponentType = null;
                buildEditArea();
            }).size(24f).pad(4f);
            t.button(Icon.edit, Styles.cleari, this::showScriptDialog).size(24f).pad(4f);
            t.row();

            // 当前值
            t.add("当前: " + formatValue(current)).color(Color.lightGray).fontScale(0.8f).colspan(4).left().row();

            // FIX: 判断可编辑性
            boolean editable;
            if(editingArray != null){
                editable = true;  // 数组元素默认可编辑
            } else {
                editable = isEditable(selectedField);
            }

            if(editable){
                TextField input = new TextField(formatValueRaw(current));
                t.add(input).growX().colspan(10).pad(4f).row();

                Object finalCurrent = current;
                Class<?> finalType = type;

                t.table(btns -> {
                    btns.left();

                    if(finalType == int.class || finalType == Integer.class){
                        btns.button("-10", () -> modifyValue(finalCurrent, -10)).size(90, 40).pad(4f);
                        btns.button("-1", () -> modifyValue(finalCurrent, -1)).size(90, 40).pad(4f);
                        btns.button("+1", () -> modifyValue(finalCurrent, 1)).size(90, 40).pad(4f);
                        btns.button("+10", () -> modifyValue(finalCurrent, 10)).size(90, 40).pad(4f);
                    } else if(finalType == float.class || finalType == Float.class || finalType == double.class || finalType == Double.class){
                        btns.button("-10", () -> modifyValue(finalCurrent, -10f)).size(90, 40).pad(4f);
                        btns.button("-0.1", () -> modifyValue(finalCurrent, -0.1f)).size(90, 40).pad(4f);
                        btns.button("+0.1", () -> modifyValue(finalCurrent, 0.1f)).size(90, 40).pad(4f);
                        btns.button("+10", () -> modifyValue(finalCurrent, 10f)).size(90, 40).pad(4f);
                    } else if(finalType == boolean.class || finalType == Boolean.class){
                        btns.button("翻转", () -> {
                            try {
                                boolean newVal = !(Boolean)finalCurrent;
                                if(editingArray != null){
                                    java.lang.reflect.Array.setBoolean(editingArray, editingArrayIndex, newVal);
                                } else {
                                    selectedField.setBoolean(selectedTarget, newVal);
                                }
                                refresh();
                            } catch(Exception ignored){}
                        }).growX().height(40).colspan(10).pad(4f);
                    } else if(finalType == Item.class){
                        for(int i=0;i<Vars.content.items().size;i++){
                            if(i!=0 && i%8 == 0) {
                                btns.row();
                            }
                            Item e = Vars.content.item(i);
                            int id = i;
                            String icon = e.emoji().isEmpty()?e.localizedName.substring(0,12):e.emoji();
                            btns.button(icon,()->{
                                applyValue(""+id);
                            });
                        }
                    } else if(finalType == Liquid.class){
                        for(int i=0;i<Vars.content.liquids().size;i++){
                            if(i!=0 && i%8 == 0) {
                                btns.row();
                            }
                            Liquid e = Vars.content.liquid(i);
                            int id = i;
                            String icon = e.emoji().isEmpty()?e.localizedName.substring(0,12):e.emoji();
                            btns.button(icon,()->{
                                applyValue(""+id);
                            });
                        }
                    } else if(finalType == Color.class){
                        btns.button(Icon.pencil, Styles.cleari, () -> {
                            ui.picker.show((Color) finalCurrent, true, res -> applyValue( String.valueOf( res.rgba()) ) );
                        }).size(40f);
                    }

                    btns.row();

                    btns.button("应用", Icon.ok, () -> {
                        applyValue(input.getText());
                    }).growX().height(40).colspan(10).padTop(4f);
                }).growX().colspan(10).pad(4f).row();
            } else {
                t.add("[只读]").color(Color.gray).fontScale(0.8f).left();
            }
        }).pad(6f).growX();
    }

    private boolean isEditable(Field f){
        int mod = f.getModifiers();
        if(Modifier.isFinal(mod) && (f.getType().isPrimitive() || f.getType() == String.class)){
            return false;
        }
        return true;
    }

    private void modifyValue(Object current, Number delta){
        try {
            Class<?> type;
            if(editingArray != null){
                type = editingArrayComponentType;
            } else {
                type = selectedField.getType();
            }

            if(type == int.class || type == Integer.class){
                int newVal = ((Number)current).intValue() + delta.intValue();
                if(editingArray != null){
                    java.lang.reflect.Array.setInt(editingArray, editingArrayIndex, newVal);
                } else {
                    selectedField.setInt(selectedTarget, newVal);
                }
            } else if(type == float.class || type == Float.class){
                float newVal = ((Number)current).floatValue() + delta.floatValue();
                if(editingArray != null){
                    java.lang.reflect.Array.setFloat(editingArray, editingArrayIndex, newVal);
                } else {
                    selectedField.setFloat(selectedTarget, newVal);
                }
            } else if(type == double.class || type == Double.class){
                double newVal = ((Number)current).doubleValue() + delta.doubleValue();
                if(editingArray != null){
                    java.lang.reflect.Array.setDouble(editingArray, editingArrayIndex, newVal);
                } else {
                    selectedField.setDouble(selectedTarget, newVal);
                }
            } else if(type == long.class || type == Long.class){
                long newVal = ((Number)current).longValue() + delta.longValue();
                if(editingArray != null){
                    java.lang.reflect.Array.setLong(editingArray, editingArrayIndex, newVal);
                } else {
                    selectedField.setLong(selectedTarget, newVal);
                }
            }
            refresh();
        } catch(Exception e){
            Log.err(e);
        }
    }

    private void applyValue(String text){
        try {
            Class<?> type;
            if(editingArray != null){
                type = editingArrayComponentType;
            } else {
                type = selectedField.getType();
            }

            Object val;

            if(type == int.class || type == Integer.class){
                val = Integer.parseInt(text);
            } else if(type == float.class || type == Float.class){
                val = Float.parseFloat(text);
            } else if(type == double.class || type == Double.class){
                val = Double.parseDouble(text);
            } else if(type == long.class || type == Long.class){
                val = Long.parseLong(text);
            } else if(type == boolean.class || type == Boolean.class){
                val = Boolean.parseBoolean(text);
            } else if(type == String.class){
                val = text;
            } else if(type == Vec2.class){
                String[] parts = text.split(",");
                float x = Float.parseFloat(parts.length > 0 ? parts[0].trim() : "0");
                float y = Float.parseFloat(parts.length > 1 ? parts[1].trim() : "0");
                val = new Vec2(x, y);
            } else if(type == Item.class){
                int id = Integer.parseInt(text);
                val = Vars.content.item(id);
            }else if(type == Liquid.class){
                int id = Integer.parseInt(text);
                val = Vars.content.liquid(id);
            }else if(type == Color.class){
                int hex = Integer.parseInt(text);
                val = new Color(hex);
            }else{
                if(text.equals("null")) val = null;
                else {
                    Log.warn("Complex type set not supported: @", type);
                    return;
                }
            }

            if(editingArray != null){
                if(type.isPrimitive()){
                    if(type == int.class) java.lang.reflect.Array.setInt(editingArray, editingArrayIndex, (Integer)val);
                    else if(type == float.class) java.lang.reflect.Array.setFloat(editingArray, editingArrayIndex, (Float)val);
                    else if(type == double.class) java.lang.reflect.Array.setDouble(editingArray, editingArrayIndex, (Double)val);
                    else if(type == long.class) java.lang.reflect.Array.setLong(editingArray, editingArrayIndex, (Long)val);
                    else if(type == boolean.class) java.lang.reflect.Array.setBoolean(editingArray, editingArrayIndex, (Boolean)val);
                    else if(type == byte.class) java.lang.reflect.Array.setByte(editingArray, editingArrayIndex, ((Number)val).byteValue());
                    else if(type == short.class) java.lang.reflect.Array.setShort(editingArray, editingArrayIndex, ((Number)val).shortValue());
                    else if(type == char.class) java.lang.reflect.Array.setChar(editingArray, editingArrayIndex, text.charAt(0));
                } else {
                    java.lang.reflect.Array.set(editingArray, editingArrayIndex, val);
                }
            } else {
                selectedField.set(selectedTarget, val);
            }
            refresh();
        } catch(Exception e){
            Log.err("Failed to apply value: @", e.getMessage());
        }
    }

    private String formatValue(Object val){
        if(val == null) return "null";
        if(val instanceof String) return "\"" + val + "\"";
        if(val instanceof Number) return val.toString();
        if(val instanceof Boolean) return val.toString();
        if(val.getClass().isArray()) return val.getClass().getComponentType().getSimpleName() + "[" + java.lang.reflect.Array.getLength(val) + "]";
        if(val instanceof Collection) return val.getClass().getSimpleName() + "(" + ((Collection<?>)val).size() + ")";
        if(val instanceof Map) return val.getClass().getSimpleName() + "{" + ((Map<?,?>)val).size() + "}";

        String cls = val.getClass().getSimpleName();
        if(cls.isEmpty()) cls = val.getClass().getName();
        String hash = Integer.toHexString(val.hashCode());
        if(hash.length() > 4) hash = hash.substring(0, 4);
        return cls + "@" + hash;
    }

    private String formatValueRaw(Object val){
        if(val == null) return "null";
        return val.toString();
    }

    private Color valueColor(Object val){
        if(val == null) return Color.gray;
        if(val instanceof Number) return Color.sky;
        if(val instanceof Boolean) return ((Boolean)val) ? Color.lime : Color.scarlet;
        if(val instanceof String) return Color.orange;
        return Color.white;
    }

    private String simplifyType(Class<?> type){
        if(type == int.class) return "int";
        if(type == long.class) return "long";
        if(type == float.class) return "float";
        if(type == double.class) return "double";
        if(type == boolean.class) return "bool";
        if(type == byte.class) return "byte";
        if(type == short.class) return "short";
        if(type == char.class) return "char";
        String name = type.getSimpleName();
        return name.length() > 12 ? name.substring(0, 10) + ".." : name;
    }

    private boolean isSimplifyType(Class<?> type){
        if(type == int.class) return true;
        if(type == long.class) return true;
        if(type == float.class) return true;
        if(type == double.class) return true;
        if(type == boolean.class) return true;
        if(type == byte.class) return true;
        if(type == short.class) return true;
        if(type == char.class) return true;
        return false;
    }


    static class ClassNode {
        final Class<?> clazz;
        final Seq<Field> fields = new Seq<>();
        Collapser collapser;


        ClassNode(Class<?> clazz, Object instance){
            this.clazz = clazz;
            for(Field f : clazz.getDeclaredFields()){
                if(!Modifier.isStatic(f.getModifiers())){
                    fields.add(f);
                }
            }
            fields.sort(f -> f.getName().length());
        }
    }
    static class ObjectPage{
        public Object target;
        public float paneY = 0;
        public boolean[] opens;
        ObjectPage(Object target,float paneY,boolean[] opens){
            this.target = target;
            this.paneY = paneY;
            this.opens = opens;
        }
    }

    /** 打开脚本对话框 */
    private void showScriptDialog(){
        final boolean isArrayMode = editingArray != null;
        if(!isArrayMode && (selectedField == null || selectedTarget == null)) return;

        // 快照当前编辑上下文，避免对话框回调时状态被覆盖
        final Object finalArray    = editingArray;
        final int    finalArrayIdx = editingArrayIndex;
        final Class<?> finalType   = isArrayMode ? editingArrayComponentType : selectedField.getType();
        final Field  finalField    = isArrayMode ? null : selectedField;
        final Object finalTarget   = isArrayMode ? editingArray : selectedTarget;
        final String finalName     = isArrayMode ? ("[" + editingArrayIndex + "]") : selectedField.getName();

        Object curVal;
        try {
            curVal = isArrayMode
                    ? java.lang.reflect.Array.get(finalArray, finalArrayIdx)
                    : finalField.get(finalTarget);
        } catch(Exception e){
            curVal = null;
        }
        final Object finalCurrent = curVal;

        Dialog dialog = new Dialog();
        dialog.title.setText("JS 脚本 - " + finalName);

        TextArea area = new TextArea("");
        area.setMessageText("例如: content.item(\"copper\") \ntarget.kill()\nr.getStatic(Items,\"copper\")");
        area.setPrefRows(6);

        Label result = new Label("");
        result.setWrap(true);
        result.setColor(Color.lightGray);

        dialog.cont.add("字段类型: ").color(Color.gray).left();
        dialog.cont.add(simplifyType(finalType)).color(Pal.accent).left().row();

        dialog.cont.add("当前值: ").color(Color.gray).left();
        dialog.cont.add(formatValue(finalCurrent)).color(Color.lightGray).left().row();

        dialog.cont.add("可用变量:r, target, value, field, content, Vars, Vec2, Color, Mathf, Item, Liquid")
                .color(Color.darkGray).fontScale(0.75f).colspan(2).left().padTop(4f).row();

        dialog.cont.add(area).growX().height(120f).colspan(2).pad(4f).row();
        dialog.cont.add(result).growX().colspan(2).left().pad(4f).row();

        dialog.cont.table(btns -> {
            btns.button("运行并应用", Icon.ok, () -> {
                try {
                    Object jsRes = evalScript(area.getText(), finalTarget, finalCurrent, finalType, finalField);

                    if(jsRes == null || jsRes == Context.getUndefinedValue()){
                        applyScriptResult(null, finalType,
                                isArrayMode, finalArray, finalArrayIdx, finalField, finalTarget);
                        result.setText("结果: null（已应用）");
                        result.setColor(Color.orange);
                        refresh();
                        return;
                    }

                    Object coerced = coerceToType(jsRes, finalType);
                    applyScriptResult(coerced, finalType,
                            isArrayMode, finalArray, finalArrayIdx, finalField, finalTarget);

                    result.setText("成功: " + formatValue(coerced));
                    result.setColor(Color.lime);
                    refresh();
                } catch(Throwable e){
                    result.setText(e.getClass().getSimpleName() + ": " + e.getMessage());
                    result.setColor(Color.scarlet);
                    Log.err(e);
                }
            }).size(140f, 40f).pad(4f);

            btns.button("仅运行", Icon.play, () -> {
                try {
                    Object jsRes = evalScript(area.getText(), finalTarget, finalCurrent, finalType, finalField);
                    Object display = (jsRes == null || jsRes == Context.getUndefinedValue())
                            ? null : Context.jsToJava(jsRes, Object.class);
                    result.setText("结果: " + formatValue(display));
                    result.setColor(Color.lightGray);
                } catch(Throwable e){
                    result.setText(e.getClass().getSimpleName() + ": " + e.getMessage());
                    result.setColor(Color.scarlet);
                    Log.err(e);
                }
            }).size(120f, 40f).pad(4f);

            btns.button("关闭", dialog::hide).size(100f, 40f).pad(4f);
        }).growX().padTop(4f).row();

        dialog.show();
    }

    /** 在 Rhino 里执行脚本，返回原始 JS 结果 */
    private Object evalScript(String script, Object targetObj, Object value,
                              Class<?> expectedType, Field field){
        Context cx = Context.getCurrentContext();
        boolean newContext = cx == null;
        if(newContext) cx = Context.enter();

        try{
            cx.setOptimizationLevel(-1);
            cx.setLanguageVersion(Context.VERSION_ES6);

            Scriptable scope = cx.initStandardObjects();
            ScriptableObject.putProperty(scope, "r",  Context.javaToJS(this.r, scope));

            ScriptableObject.putProperty(scope, "target",  Context.javaToJS(targetObj, scope));
            ScriptableObject.putProperty(scope, "value",   Context.javaToJS(value, scope));
            ScriptableObject.putProperty(scope, "field",   Context.javaToJS(field, scope));
            ScriptableObject.putProperty(scope, "type",    Context.javaToJS(expectedType, scope));
            ScriptableObject.putProperty(scope, "content", Context.javaToJS(Vars.content, scope));
            ScriptableObject.putProperty(scope, "Vars",    Context.javaToJS(Vars.class, scope));

            // 常用工具类，脚本里可以直接 new / 调用
            ScriptableObject.putProperty(scope, "Vec2",   Context.javaToJS(Vec2.class,   scope));
            ScriptableObject.putProperty(scope, "Color",  Context.javaToJS(Color.class,  scope));
            ScriptableObject.putProperty(scope, "Mathf",  Context.javaToJS(Mathf.class,  scope));
            ScriptableObject.putProperty(scope, "Item",   Context.javaToJS(Item.class,   scope));
            ScriptableObject.putProperty(scope, "Items", Context.javaToJS(Items.class, scope));
            ScriptableObject.putProperty(scope, "Liquid", Context.javaToJS(Liquid.class, scope));
            ScriptableObject.putProperty(scope, "Liquids", Context.javaToJS(Liquids.class, scope));
            ScriptableObject.putProperty(scope, "Sound", Context.javaToJS(Sound.class, scope));
            ScriptableObject.putProperty(scope, "Sounds", Context.javaToJS(Sounds.class, scope));
            ScriptableObject.putProperty(scope, "Fx", Context.javaToJS(Fx.class, scope));
            ScriptableObject.putProperty(scope, "Effect", Context.javaToJS(Effect.class, scope));

            return cx.evaluateString(scope, script, "field-script", 1);
        } finally {
            if(newContext) Context.exit();
        }
    }

    /** 把 JS 结果尽量转成目标 Java 类型 */
    private Object coerceToType(Object jsResult, Class<?> type){
        if(jsResult == null || jsResult == Context.getUndefinedValue()
                || jsResult == Scriptable.NOT_FOUND) return null;

        if(type.isInstance(jsResult)) return jsResult;

        try{
            return Context.jsToJava(jsResult, type);
        }catch(Throwable e){
            Log.err("coerce failed to " + type.getSimpleName(), e);
            return jsResult; // 交给 Field.set / Array.set 去抛异常
        }
    }

    /** 把结果写回字段或数组元素 */
    private void applyScriptResult(Object result, Class<?> type,
                                   boolean isArray, Object arr, int arrIdx,
                                   Field field, Object targetObj){
        try{
            if(isArray){
                if(result == null && type.isPrimitive()){
                    Log.warn("Cannot set primitive array element to null");
                    return;
                }
                java.lang.reflect.Array.set(arr, arrIdx, result);
            } else if(field != null && targetObj != null){
                field.setAccessible(true);
                field.set(targetObj, result);
            }
        }catch(Throwable e){
            Log.err("apply script result failed", e);
        }
    }
    public class ReflectUtil {
        public ReflectUtil(){}

        private static Class<?> cls(Object c){
            if(c instanceof Class) return (Class<?>)c;
            if(c instanceof String){
                try { return Class.forName((String)c); }
                catch(ClassNotFoundException e){ throw new RuntimeException(e); }
            }
            return c.getClass();
        }

        private static Field findField(Class<?> c, String name){
            while(c != null){
                try { return c.getDeclaredField(name); }
                catch(NoSuchFieldException ignored){}
                c = c.getSuperclass();
            }
            throw new RuntimeException("No field: " + name);
        }

        private static Method findMethod(Class<?> c, String name, Object[] args, boolean isStatic){
            Class<?>[] types = new Class<?>[args == null ? 0 : args.length];
            for(int i = 0; i < types.length; i++){
                types[i] = args[i] == null ? null : args[i].getClass();
            }
            while(c != null){
                for(Method m : c.getDeclaredMethods()){
                    if(!m.getName().equals(name)) continue;
                    if(Modifier.isStatic(m.getModifiers()) != isStatic) continue;
                    Class<?>[] ps = m.getParameterTypes();
                    if(ps.length != types.length) continue;
                    boolean ok = true;
                    for(int i = 0; i < ps.length; i++){
                        if(types[i] != null && !box(ps[i]).isAssignableFrom(types[i])){ ok = false; break; }
                    }
                    if(ok) return m;
                }
                c = c.getSuperclass();
            }
            throw new RuntimeException("No method: " + name);
        }

        private static Class<?> box(Class<?> c){
            if(c == int.class) return Integer.class;
            if(c == long.class) return Long.class;
            if(c == float.class) return Float.class;
            if(c == double.class) return Double.class;
            if(c == boolean.class) return Boolean.class;
            if(c == byte.class) return Byte.class;
            if(c == short.class) return Short.class;
            if(c == char.class) return Character.class;
            return c;
        }

        // ---------- 对外方法 ----------

        public Object getStatic(Object cls, String name){
            try {
                Field f = findField(cls(cls), name);
                f.setAccessible(true);
                return f.get(null);
            } catch(Exception e){ Log.err(e); return null; }
        }

        public void setStatic(Object cls, String name, Object val){
            try {
                Field f = findField(cls(cls), name);
                f.setAccessible(true);
                f.set(null, val);
            } catch(Exception e){ Log.err(e); }
        }

        public Object callStatic(Object cls, String name, Object[] args){
            try {
                Method m = findMethod(cls(cls), name, args, true);
                m.setAccessible(true);
                return m.invoke(null, args);
            } catch(Exception e){ Log.err(e); return null; }
        }

        public Object getField(Object obj, String name){
            try {
                Field f = findField(obj.getClass(), name);
                f.setAccessible(true);
                return f.get(obj);
            } catch(Exception e){ Log.err(e); return null; }
        }

        public void setField(Object obj, String name, Object val){
            try {
                Field f = findField(obj.getClass(), name);
                f.setAccessible(true);
                f.set(obj, val);
            } catch(Exception e){ Log.err(e); }
        }

        public Object call(Object obj, String name, Object[] args){
            try {
                Method m = findMethod(obj.getClass(), name, args, false);
                m.setAccessible(true);
                return m.invoke(obj, args);
            } catch(Exception e){ Log.err(e); return null; }
        }
    }
}