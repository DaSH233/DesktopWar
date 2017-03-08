package com.war.desktop.desktopwar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by czy on 2016/12/20.
 */
public class GameView extends View {

    private final static float sqrt3 = (float) Math.sqrt(3);
    private final static int line_length = 200;
    private boolean move_flag = false;
    private boolean highlight_flag = false;
    private boolean selected = false;
    private boolean attacking = false;
    private boolean waiting = false;
    private int ctrl_group;
    private int focus_order = 0;
    private int window_width;
    private int window_height;
    private Context context;
    private float oldx;
    private float oldy;
    private float old_distance;
    private float translate_x = 0;
    private float translate_y = 0;
    private float scale_rate = 1;
    private Paint paint = new Paint();
    private Paint paint_highlight = new Paint();
    private Hexagon highlight_atk = new Hexagon();
    private Hexagon highlight_def = new Hexagon();
    private List<Hexagon> atk_area = new ArrayList<>();
    private List<Hexagon> hexagons = new ArrayList<>();
    private List<Button> btn = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private GameMap gameMap = new GameMap(7);
    private int max_xy = 0;
    private int min_xy = 0;
    private Bitmap[] group = new Bitmap[5];
    private Bitmap[] rank = new Bitmap[5];
    private Bitmap[] type = new Bitmap[4];

    public GameView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        this.context = context;
        paint.setColor(Color.BLACK);
        paint.setPathEffect(new CornerPathEffect(20));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(23);
        paint_highlight.setColor(Color.YELLOW);
        paint_highlight.setPathEffect(new CornerPathEffect(20));
        paint_highlight.setStyle(Paint.Style.STROKE);
        paint_highlight.setStrokeWidth(23);
        loadMap();
        loadbmp();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int i;
        window_width = canvas.getWidth();
        window_height = canvas.getHeight();
        canvas.drawRGB(255, 255, 255);
        canvas.translate(window_width / 2 + translate_x, window_height / 2 + translate_y);
        canvas.scale(scale_rate, scale_rate);
        for(i = 0; i < hexagons.size(); i++) {
            if(hexagons.get(i).getGroup() >= 0) {
                canvas.drawBitmap(group[hexagons.get(i).getGroup()], hexagons.get(i).getXYCenter(line_length).x - line_length * sqrt3 / 2, hexagons.get(i).getXYCenter(line_length).y - line_length, null);
                if(hexagons.get(i).getRank() > 0)
                    canvas.drawBitmap(rank[hexagons.get(i).getRank()], hexagons.get(i).getXYCenter(line_length).x - line_length * sqrt3 / 2, hexagons.get(i).getXYCenter(line_length).y - line_length, null);
                if(hexagons.get(i).getType() > 0)
                    canvas.drawBitmap(type[hexagons.get(i).getType()], hexagons.get(i).getXYCenter(line_length).x - line_length * sqrt3 / 2, hexagons.get(i).getXYCenter(line_length).y - line_length, null);
                canvas.drawPath(hexagons.get(i).getPath(), paint);
            }
        }
        paint_highlight.setColor(Color.GREEN);
        for(i = 0; i < atk_area.size(); i++)
            canvas.drawPath(atk_area.get(i).getPath(), paint_highlight);
        paint_highlight.setColor(Color.RED);
        canvas.drawPath(highlight_def.getPath(), paint_highlight);
        paint_highlight.setColor(Color.YELLOW);
        canvas.drawPath(highlight_atk.getPath(), paint_highlight);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            oldx = x;
            oldy = y;
            highlight_flag = true;
            if (!waiting) {
                Point pos;
                int order;
                pos = getHexagonPos((x - window_width / 2 - translate_x) / scale_rate, (y - window_height / 2 - translate_y) / scale_rate);
                order = cal_order(pos.x, pos.y);
                if (pos.x >= min_xy && pos.x <= max_xy && pos.y >= min_xy && pos.y <= max_xy && hexagons.get(order).getGroup() >= 0) {
                    highlight_def.set(hexagons.get(order));
                }
                invalidate();
            }
            return true;
        }
        else if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_DOWN) {
            old_distance = cal_distance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
            return true;
        }
        else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            highlight_def.reset();
            if(highlight_flag && !waiting) {
                Point pos;
                int order;
                int i;
                pos = getHexagonPos((x  - window_width / 2 - translate_x) / scale_rate, (y - window_height / 2 - translate_y) / scale_rate);
                order = cal_order(pos.x, pos.y);
                if(attacking) {
                    for (i = 0; i < atk_area.size(); i++) {
                        if (atk_area.get(i).getCenter().equals(pos)) {
                            do_atk(order);
                            atk_area.clear();
                            highlight_atk.reset();
                            selected = false;
                            attacking = false;
                            btn.get(2).animate().scaleX(1);
                            btn.get(2).animate().scaleY(1);
                            btn.get(2).setEnabled(true);
                            btn.get(3).animate().scaleX(0);
                            btn.get(3).animate().scaleY(0);
                            btn.get(3).setEnabled(false);
                            break;
                        }
                    }
                }
                else if (hexagons.get(order).getGroup() == ctrl_group && !highlight_atk.getCenter().equals(pos) && pos.x >= min_xy && pos.x <= max_xy && pos.y >= min_xy && pos.y <= max_xy && hexagons.get(order).getGroup() >= 0) {
                    highlight_atk.set(hexagons.get(order));
                    atk_area.clear();
                    focus_order = order;
                    if(!selected) {
                        for(i = 0; i < 2; i++) {
                            btn.get(i).animate().scaleX(1);
                            btn.get(i).animate().scaleY(1);
                            btn.get(i).setEnabled(true);
                        }
                        selected = true;
                    }
                } else {
                    highlight_atk.reset();
                    atk_area.clear();
                    if(selected) {
                        for (i = 0; i < 2; i++) {
                            btn.get(i).animate().scaleX(0);
                            btn.get(i).animate().scaleY(0);
                            btn.get(i).setEnabled(false);
                        }
                        selected = false;
                    }
                }
                highlight_flag = false;
            }
        }
        else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_POINTER_UP) {
            move_flag = true;
        }
        else if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_MOVE) {
            if(event.getPointerCount() == 1) {
                if(move_flag) {
                    oldx = x;
                    oldy = y;
                    move_flag = false;
                }
                if(Math.abs(x - oldx) > 5 && Math.abs(y - oldy) > 5)
                    highlight_flag = false;
                if(Math.abs(translate_x) <= 1500 * scale_rate && Math.abs(translate_y) <= 1500 * scale_rate) {
                    translate_x += x - oldx;
                    translate_y += y - oldy;
                }
                else if(translate_x < -1500 * scale_rate) {
                    translate_x = -1500 * scale_rate;
                }
                else if(translate_x > 1500 * scale_rate) {
                    translate_x = 1500 * scale_rate;
                }
                else if(translate_y < -1500 * scale_rate) {
                    translate_y = -1500 * scale_rate;
                }
                else if(translate_y > 1500 * scale_rate) {
                    translate_y = 1500 * scale_rate;
                }
                oldx = x;
                oldy = y;
            }
            else {
                float point_distance;
                point_distance = cal_distance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                if(scale_rate >= 0.5 && scale_rate <= 2) {
                    scale_rate += (point_distance - old_distance) / 1000;
                }
                else if (scale_rate < 0.5) {
                    scale_rate = (float)0.5;
                }
                else if (scale_rate > 2) {
                    scale_rate = (float)2;
                }
                old_distance = point_distance;
                highlight_flag = false;
            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }

    public void Buttonlistener (int btn_num) {
        if(btn_num == 0) {
            btn.get(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int i;
                    Point pos = new Point(hexagons.get(focus_order).getCenter());
                    List<Point> near_pos = new ArrayList<>();
                    int near_order;
                    near_pos.add(new Point(pos.x - 1, pos.y + 1));
                    near_pos.add(new Point(pos.x, pos.y + 1));
                    near_pos.add(new Point(pos.x + 1, pos.y));
                    near_pos.add(new Point(pos.x + 1, pos.y - 1));
                    near_pos.add(new Point(pos.x, pos.y - 1));
                    near_pos.add(new Point(pos.x - 1, pos.y));
                    for (i = 0; i < 6; i++) {
                        near_order = cal_order(near_pos.get(i).x, near_pos.get(i).y);
                        if (near_pos.get(i).x >= min_xy && near_pos.get(i).x <= max_xy && near_pos.get(i).y >= min_xy && near_pos.get(i).y <= max_xy) {
                            if (hexagons.get(near_order).getGroup() != -1 && hexagons.get(near_order).getGroup() != hexagons.get(focus_order).getGroup()) {
                                atk_area.add(hexagons.get(near_order));
                            }
                        }
                    }
                    for (i = 0; i < 3; i++) {
                        btn.get(i).animate().scaleX(0);
                        btn.get(i).animate().scaleY(0);
                        btn.get(i).setEnabled(false);
                    }
                    btn.get(3).animate().scaleX(1);
                    btn.get(3).animate().scaleY(1);
                    btn.get(3).setEnabled(true);
                    attacking = true;
                    invalidate();
                }
            });
        }
        else if(btn_num == 1) {
            btn.get(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    atk_area.clear();
                    if (hexagons.get(focus_order).getGroup() > 0 && hexagons.get(focus_order).getRank() < 4)
                        hexagons.get(focus_order).setRank(hexagons.get(focus_order).getRank() + 1);
                    invalidate();
                }
            });
        }
        else if(btn_num == 2) {
            btn.get(2).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    atk_area.clear();
                    highlight_atk.reset();
                    attacking = false;
                    selected = false;
                    waiting = true;
                    for (int i = 0; i < 4; i++) {
                        btn.get(i).animate().scaleX(0);
                        btn.get(i).animate().scaleY(0);
                        btn.get(i).setEnabled(false);
                    }
                    //invalidate();
                    AI_do();
                    new Handler().postDelayed(new Runnable(){
                        public void run() {
                            btn.get(2).animate().scaleX(1);
                            btn.get(2).animate().scaleY(1);
                            btn.get(2).setEnabled(true);
                            waiting = false;
                        }
                    }, 2000);
                }
            });
        }
        else if(btn_num == 3) {
            btn.get(3).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    atk_area.clear();
                    attacking = false;
                    for (int i = 0; i < 3; i++) {
                        btn.get(i).animate().scaleX(1);
                        btn.get(i).animate().scaleY(1);
                        btn.get(i).setEnabled(true);
                    }
                    btn.get(3).animate().scaleX(0);
                    btn.get(3).animate().scaleY(0);
                    btn.get(3).setEnabled(false);
                    invalidate();
                }
            });
        }
    }

    public void AI_do(){
    }

    public void do_atk(int def_order) {
        int atk_order = cal_order(highlight_atk.getCenter().x, highlight_atk.getCenter().y);
        int atk_num = (int)((1 + Math.random() * 6) * hexagons.get(atk_order).getRank());
        int def_num = (int)((1 + Math.random() * 6) * hexagons.get(def_order).getRank());
        Toast.makeText(context,String.valueOf(atk_num) + " : " + String.valueOf(def_num), Toast.LENGTH_SHORT).show();
        if(atk_num > def_num) {
            hexagons.get(def_order).setGroup(hexagons.get(atk_order).getGroup());
            if(hexagons.get(atk_order).getRank() > 1)
                hexagons.get(def_order).setRank(hexagons.get(atk_order).getRank() - 1);
            else
                hexagons.get(def_order).setRank(1);
            hexagons.get(def_order).setType(hexagons.get(atk_order).getType());
            hexagons.get(atk_order).setRank(1);
        }
        else if(atk_num < def_num) {
            if(hexagons.get(atk_order).getRank() <= hexagons.get(def_order).getRank()) {
                if(hexagons.get(atk_order).getRank() == 1) {
                    hexagons.get(atk_order).setGroup(0);
                    hexagons.get(atk_order).setRank(0);
                    hexagons.get(atk_order).setType(0);
                }
                else {
                    hexagons.get(atk_order).setRank(1);
                }
            }
            else {
                hexagons.get(atk_order).setRank(hexagons.get(atk_order).getRank() - hexagons.get(def_order).getRank());
            }
        }
    }

    public int cal_order(int x, int y) {
        return (y - min_xy) * gameMap.getMap_area() + x - min_xy;
    }

    public float cal_distance(float x1, float y1, float x2, float y2) {
        float sub_x = x1 - x2;
        float sub_y = y1 - y2;
        return  (float) Math.sqrt(sub_x * sub_x + sub_y * sub_y);
    }

    public Point getHexagonPos(float x, float y){
        Point hexagon = new Point();
        x = x / line_length;
        y = y / line_length;
        float floatX = x / sqrt3;
        int intX = Math.round(floatX);
        double deltaX = Math.abs(floatX - intX) * sqrt3;
        int flagX = floatX - intX < 0 ? 1 : 0;
        double offset = 1 - sqrt3 / 3 * deltaX;
        if (flagX == 0)
            offset = -offset;
        double doubleY = (y - offset) / 3;
        int intY = (int)Math.round(Math.floor(doubleY));
        hexagon.x = intX - (intY + flagX);
        double deltaY = y - 3 * intY - offset;
        double YBound;
        if (flagX == 1)
            YBound = 1 + 2 * sqrt3 / 3 * deltaX;
        else
            YBound = 3 - (1 + 2 * sqrt3 / 3 * deltaX);
        int flagY = deltaY > YBound ? 1 : 0;
        hexagon.y = 2 * intY + flagX + flagY;
        return hexagon;
    }

    public void loadbmp() {
        int width = (int)(line_length * sqrt3);
        int height = line_length * 2;
        group[0] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.g0), width, height, true);
        group[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.g1), width, height, true);
        group[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.g2), width, height, true);
        group[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.g3), width, height, true);
        group[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.g4), width, height, true);

        rank[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.rank1), width, height, true);
        rank[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.rank2), width, height, true);
        rank[3] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.rank3), width, height, true);
        rank[4] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.rank4), width, height, true);

        type[1] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.t1), width, height, true);
        type[2] = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.t2), width, height, true);
    }

    public void addButton (Button new_btn) {
        btn.add(new_btn);
        Buttonlistener(btn.size() - 1);
    }

    public void setMap (GameMap new_gameMap) {
        gameMap.setGameMap(new_gameMap);
        min_xy = -gameMap.getMap_area() / 2;
        max_xy = (gameMap.getMap_area() - 1) / 2;
        loadMap();
    }

    public void setCtrl(int group){
        ctrl_group = group;
    }

    public void loadMap() {
        int i, j;
        float center_x, center_y;
        hexagons.clear();
        for(j = min_xy; j <= max_xy; j++) {
            for(i = min_xy; i <= max_xy; i++) {
                center_x = (i + (float)j / 2) * line_length * sqrt3;
                center_y = (j * sqrt3 / 2) * line_length * sqrt3;
                Path path = new Path();
                path.moveTo(center_x, center_y + line_length);
                path.lineTo(center_x + line_length * sqrt3 / 2, center_y + line_length / 2);
                path.lineTo(center_x + line_length * sqrt3 / 2, center_y - line_length / 2);
                path.lineTo(center_x, center_y - line_length);
                path.lineTo(center_x - line_length * sqrt3 / 2, center_y - line_length / 2);
                path.lineTo(center_x - line_length * sqrt3 / 2, center_y + line_length / 2);
                path.close();
                hexagons.add(new Hexagon(path, new Point(i, j), gameMap.getGroup()[j - min_xy][i - min_xy], gameMap.getRank()[j - min_xy][i - min_xy], gameMap.getType()[j - min_xy][i - min_xy]));
            }
        }
    }
}
