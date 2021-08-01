package operation;

import java.io.Serializable;
import java.util.*;

public class MultiVector implements Serializable{
    public ArrayList<Integer> _shape = new ArrayList<>();
    public ArrayList<Integer> _component_eNum = new ArrayList<>();
    ArrayList<Integer> temp_shape = new ArrayList<>(); //used in calculation when boradcast is needed!
    ArrayList<Integer> temp_component_eNum = new ArrayList<>(); //used in calculation when boradcast is needed!
    public double[] _data;
    public int total_eNum = 0;
    public int _dims; //total dim length
    public boolean needBroadcast = false;

    public MultiVector(double _v)
    {
        this._data = new double[1];
        this._data[0] = _v;
        this._shape.add(1);
        this._component_eNum.add(1);
        this.total_eNum = 1;
        this._dims = 1;
    }

    public MultiVector(MultiVector _mv)
    {
        assert _mv != null;
        this._data = new double[_mv.total_eNum];
        this.total_eNum = _mv.total_eNum;
        this._dims = _mv._dims;
        for(int i = 0; i < _mv._dims; i++)
        {
            this._shape.add(_mv._shape.get(i));
            this._component_eNum.add(_mv._component_eNum.get(i));
        }
        if (this.total_eNum >= 0) System.arraycopy(_mv._data, 0, this._data, 0, this.total_eNum);
    }

    public MultiVector(int[] dims_size){
        for (int j : dims_size) {
            this._shape.add(j);
        }
        this._dims = this._shape.size();
        this.total_eNum = getElementsNum();
        int tot = this.total_eNum;
        for (Integer integer : _shape) {
            this._component_eNum.add(tot / integer);
            tot /= integer;
        }
        this._data = new double[this.total_eNum];
    }

    public MultiVector(int[] dims_size, int initial_type)
    {
        for (int j : dims_size) {
            this._shape.add(j);
        }
        this._dims = this._shape.size();
        this.total_eNum = getElementsNum();
        int tot = this.total_eNum;
        for (Integer integer : _shape) {
            this._component_eNum.add(tot / integer);
            tot /= integer;
        }
        initializeData(initial_type);
    }


    public void initializeData(int initial_type){
        if(initial_type == Calculation.SET_EMPTY_DATA){
            return;
        }
        if(this._data == null) this._data = new double[this.total_eNum];
        switch(initial_type)
        {
            case Calculation.SET_ALL_ZEROS:{
                for(int i = 0; i < this.total_eNum; i++)
                {
                    this._data[i] = 0.0;
                }
                break;
            }
            case Calculation.SET_ALL_ONES:
            {
                for(int i = 0; i < this.total_eNum; i++)
                {
                    this._data[i] = 1.0;
                }
                break;
            }
            case Calculation.SET_INCREASE:
            {
                for(int i = 0; i < this.total_eNum; i++)
                {
                    this._data[i] = (i + 1) * 1.0;
                }
                break;
            }
            case Calculation.SET_DOUBLE_MAX:
            {
                for(int i = 0; i < this.total_eNum; i++)
                {
                    this._data[i] = Double.MAX_VALUE;
                }
                break;
            }
            case Calculation.SET_DOUBLE_MIN:
            {
                for(int i = 0; i < this.total_eNum; i++)
                {
                    this._data[i] = Double.MIN_VALUE;
                }
                break;
            }
            case Calculation.SET_RANDOM_UINT16:
            {
                Random rand = new Random();
                for(int i = 0; i < this.total_eNum; i++)
                {
                    this._data[i] = rand.nextInt(1 << 16);
                }
                break;
            }
            default:
            { }
        }
    }

    public MultiVector(ArrayList<Integer> dims_size)
    {
        this(dims_size.stream().mapToInt(Integer::valueOf).toArray());
    }

    public MultiVector(ArrayList<Integer> dims_size, int initial_type)
    {
        this(dims_size.stream().mapToInt(Integer::valueOf).toArray(), initial_type);
    }

    public int getElementsNum()
    {
        if(this._shape.isEmpty()) return 0;
        else
        {
            int res = 1;
            for(int ds : this._shape) res *= ds;
            return res;
        }
    }

    public void set_zeros()
    {
        for(int i = 0; i < total_eNum; i++)
        {
            _data[i] = 0.0;
        }
    }

    public void set_ones()
    {
        for(int i = 0; i < total_eNum; i++)
        {
            _data[i] = 1.0;
        }
    }

    /*currently, this method is allowed to set the Multivector with another one, and
    * the first dim size, aka shape[0] is allowed to be different, refers to the batch size.
    * */
    public void set_with(MultiVector mv1){
        try {
            _set_with(mv1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void _set_with(MultiVector mv1) {
        boolean sameShape = false;
        if(this._dims == mv1._dims){
            sameShape = true;
            for(int i = 0; i < this._dims; i++){
                if(!this._shape.get(i).equals(mv1._shape.get(i))){
                    sameShape = false;
                    break;
                }
            }
        }
        if(sameShape){
            System.arraycopy(mv1._data, 0, this._data, 0, this.total_eNum);
            return;
        }
        this._data = new double[mv1.total_eNum];
        this._data = Arrays.copyOf(mv1._data, mv1.total_eNum);
        this._dims = mv1._dims;
        this.total_eNum = mv1.total_eNum;
        ArrayList<Integer> new_shape = new ArrayList<>(mv1._shape);
        ArrayList<Integer> new_component_eNum = new ArrayList<>(mv1._component_eNum);
        this._shape = new_shape;
        this._component_eNum = new_component_eNum;
    }

    public void showShape()
    {
        int shpL = this._shape.size();
        if(shpL == 0){
            System.out.println("shape = ()");
            return;
        }
        System.out.print("shape = (");
        for(int i = 0; i < shpL; i++)
        {
            System.out.print(this._shape.get(i));
            if(i < shpL - 1) System.out.print(", ");
            else System.out.print(")");
        }
        System.out.println();
    }

    public String stringShape(){
        int shpL = this._shape.size();
        if(shpL == 0){
            return "shape = ()";
        }
        StringBuilder strShape = new StringBuilder();
        strShape.append("shape = (");
        for(int i = 0; i < shpL; i++){
            strShape.append(this._shape.get(i));
            if(i < shpL - 1) strShape.append(", ");
            else strShape.append(")");
        }
        return strShape.toString();
    }

    public void print()
    {
        showShape();
        int maxLength = 0;
        for(int i = 0; i < this.total_eNum; i++)
        {
            maxLength = Math.max(maxLength, String.valueOf(this._data[i]).length());
        }

        if(this._shape.isEmpty()) System.out.println("There is no elements!");
        else if(this._shape.size() == 1)
        {
            int ed = this._shape.get(0);
            System.out.print("[");
            String format_str = "%" + (maxLength + 2) + "s";
            for(int i = 0; i < ed; i++)
            {
                if(i < ed - 1)
                {
                    System.out.printf(format_str, this._data[i] + ",");
                }
                else
                {
                    System.out.printf(format_str, this._data[i] + "]");
                }
            }
            System.out.println();
        }
        else _print(0, 0, 0, maxLength);
    }

    private void _print(int cur_dim, int idx, int _offset, int maxLength)
    {
        if(cur_dim < this._dims)
        {
            if(cur_dim == this._dims - 1)
            {
                int ed = this._shape.get(cur_dim);
                int spaces = cur_dim;
                while(spaces > 0 && idx != 0)
                {
                    System.out.print(" ");
                    spaces--;
                }
                String format_str = "%" + (maxLength + 2) + "s";
                System.out.print("[");
                for(int i = 0; i < ed; i++)
                {
                    if(i < ed - 1) System.out.printf(format_str, this._data[_offset + i] + ",");
                    else System.out.printf(format_str, this._data[_offset + i] + "]");
                }
            }
            else
            {
                int offset = _offset;
                int ed = this._shape.get(cur_dim);
                int spaces = cur_dim;
                while(spaces > 0 && idx != 0)
                {
                    System.out.print(" ");
                    spaces--;
                }
                System.out.print("[");
                for(int i = 0; i < ed; i++)
                {
                    _print(cur_dim + 1, i, offset, maxLength);
                    offset += this._component_eNum.get(cur_dim);
                    if(i < ed - 1)
                    {
                        System.out.println(",");
                        int endlines = Math.max(this._dims - cur_dim - 2, 0);
                        while(endlines > 0)
                        {
                            System.out.println();
                            endlines--;
                        }
                    }
                }
                System.out.print("]");
                if(cur_dim == 0) System.out.println();
            }
        }
    }

    public void squeeze()
    {
        ArrayList<Integer> squeeze_axes_list = new ArrayList<>();
        for(int i = 0; i < this._dims; i++)
        {
            if(this._shape.get(i) == 1) squeeze_axes_list.add(i);
        }
        int[] squeeze_axes = new int[squeeze_axes_list.size()];
        for(int i = 0; i < squeeze_axes_list.size(); i++)
        {
            squeeze_axes[i] = squeeze_axes_list.get(i);
        }
        this.squeeze(squeeze_axes);
    }
    public void squeeze(int...axes)
    {
        try{
            this._squeeze(axes);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void _squeeze(int...axes) throws Exception
    {
        ArrayList<Integer> new_shape = new ArrayList<>();
        ArrayList<Integer> new_component_eNum = new ArrayList<>();
        int new_dims;
        HashSet<Integer> hashst = new HashSet<>();
        //check
        for(int i = 0; i < axes.length; i++)
        {
            hashst.add(axes[i]);
            if(axes[i] < 0 || axes[i] >= this._dims)
            {
                throw new Exception(String.format("Axis index % d out of bound !", axes[i]));
            }
            else if(this._shape.get(axes[i]) != 1)
            {
                throw new Exception(String.format("Axis size %d(at axis %d) != 1 !", this._shape.get(axes[i]), axes[i]));
            }
        }
        for(int i = 0; i < this._dims; i++)
        {
            if(!hashst.contains(i))
            {
                new_shape.add(this._shape.get(i));
            }
        }

        if(new_shape.isEmpty()) new_shape.add(1);
        new_dims = new_shape.size();
        int tot = this.total_eNum;
        for (Integer integer : new_shape) {
            new_component_eNum.add(tot / integer);
            tot /= integer;
        }
        this._dims = new_dims;
        this._component_eNum = new_component_eNum;
        this._shape = new_shape;
    }

    public void unsqueeze(int axis)
    {
        try{
            this._unsqueeze(axis);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    private void _unsqueeze(int axis) throws Exception
    {
        // if(axes.length == 0) throw new Exception("Must designate an axis position when using unsqueeze on a MultiVector!");
        if(axis < 0 || axis > this._dims)
        {
            throw new Exception(String.format("Axis index %d is out of range, should be in [0, %d]", axis, this._dims));
        }
        if(axis < this._dims)
        {
            this._shape.add(axis, 1);
            this._component_eNum.add(axis, this._component_eNum.get(axis));
        }
        else
        {
            this._shape.add(1);
            this._component_eNum.add(1);
        }
        this._dims += 1;
    }

    public synchronized void addGrad(double c){
        this.add(c);
    }

    public synchronized void addGrad(MultiVector mv1){
        this.add(mv1);
    }

    public void add(double c)
    {
        MultiVector mv1 = new MultiVector(c);
        try{
            this._op_inplace_with_check(0, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void add(MultiVector mv1)
    {
        try{
            this._op_inplace_with_check(0, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void sub(double c)
    {
        MultiVector mv1 = new MultiVector(c);
        try{
            this._op_inplace_with_check(1, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void sub(MultiVector mv1)
    {
        try{
            this._op_inplace_with_check(1, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void mul(double c)
    {
        MultiVector mv1 = new MultiVector(c);
        try{
            this._op_inplace_with_check(2, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void mul(MultiVector mv1)
    {
        try{
            this._op_inplace_with_check(2, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void div(double c)
    {
        MultiVector mv1 = new MultiVector(c);
        try{
            this._op_inplace_with_check(3, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void div(MultiVector mv1)
    {
        try{
            this._op_inplace_with_check(3, mv1);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void _op_inplace_with_check(int op, MultiVector mv1) throws Exception
    {
        mv1.temp_shape.clear();
        mv1.temp_component_eNum.clear();

        if(mv1._shape.size() > this._shape.size())
        {
            throw new Exception(String.format("Can NOT broadcast(inplace)! The second should have smaller or equal dimensions, while the MultiVector has %d dimensions itself, the second has %d dimensions!", this._dims, mv1._dims));
        }
        int sp_self = this._shape.size() - 1;
        int sp1 = mv1._shape.size() - 1;
        boolean needBoradcast = false;
        while(sp1 >= 0)
        {
            if(!this._shape.get(sp_self).equals(mv1._shape.get(sp1)))
            {
                needBoradcast = true;
                if(mv1._shape.get(sp1) != 1) throw new Exception(String.format("Dimension NOT match and can NOT broadcast(inplace)! Only the second can broadcast, however at dimension %s and dimension %s, got dimension size %s -- %s!", sp_self, sp1, this._shape.get(sp_self), mv1._shape.get(sp1)));
            }
            mv1.temp_shape.add(mv1._shape.get(sp1));
            sp_self--;
            sp1--;
        }
        if(sp_self >= 0)
        {
            needBoradcast = true;
            while(sp_self >= 0)
            {
                mv1.temp_shape.add(1);
                sp_self--;
            }
        }

        // reverse shape list
        Collections.reverse(mv1.temp_shape);


        if(!needBoradcast)
        {
            switch(op)
            {
                case 0://add
                {
                    for(int i = 0; i < this.total_eNum; i++)
                    {
                        this._data[i] += mv1._data[i];
                    }
                    break;
                }
                case 1://sub
                {
                    for(int i = 0; i < this.total_eNum; i++)
                    {
                        this._data[i] -= mv1._data[i];
                    }
                    break;
                }
                case 2://multiply
                {
                    for(int i = 0; i < this.total_eNum; i++)
                    {
                        this._data[i] *= mv1._data[i];
                    }
                    break;
                }
                case 3://divide
                {
                    for(int i = 0; i < this.total_eNum; i++)
                    {
                        this._data[i] /= mv1._data[i];
                    }
                    break;
                }
                default:
                    System.out.println("It should never come to this default?");
            }
            return;
        }
        int tot1 = mv1.total_eNum;
        for(int i = 0; i < this._shape.size(); i++)
        {
            mv1.temp_component_eNum.add(tot1 / mv1.temp_shape.get(i));
            tot1 /= mv1.temp_shape.get(i);
        }
        _op_inplace_recur(op, mv1, 0, 0, 0, 0, 0);
    }
    private void _op_inplace_recur(int op, MultiVector mv1, int cur_dim, int self_idx, int idx1, int _self_offset, int _offset1)
    {
        if(cur_dim == this._dims - 1)
        {
            int self_ed = this._shape.get(cur_dim);
            int ed1 = mv1.temp_shape.get(cur_dim);

            switch(op)
            {
                case 0:// add
                {
                    for(int i = 0; i < self_ed; i++)
                    {
                        this._data[i + _self_offset] += mv1._data[Math.min(i, ed1 - 1) + _offset1];
                    }
                    break;
                }
                case 1:// sub
                {
                    for(int i = 0; i < self_ed; i++)
                    {
                        this._data[i + _self_offset] -= mv1._data[Math.min(i, ed1 - 1) + _offset1];
                    }
                    break;
                }
                case 2:// multiply
                {
                    for(int i = 0; i < self_ed; i++)
                    {
                        this._data[i + _self_offset] *= mv1._data[Math.min(i, ed1 - 1) + _offset1];
                    }
                    break;
                }
                case 3:// divide /* still needs to be checked */
                {
                    for(int i = 0; i < self_ed; i++)
                    {
                        this._data[i + _self_offset] /= mv1._data[Math.min(i, ed1 - 1) + _offset1];
                    }
                    break;
                }
                default:
                    throw new RuntimeException("How do you end up here???");
            }
        }
        else
        {
            int self_ed = this._shape.get(cur_dim);
            int self_offset = _self_offset;
            int offset1 = _offset1;
            for(int i = 0; i < self_ed; i++)
            {
                if(cur_dim + 1 < this._dims) _op_inplace_recur(op, mv1, cur_dim + 1, i, i, self_offset, offset1);
                self_offset += this._component_eNum.get(cur_dim);
                if(mv1.temp_shape.get(cur_dim) != 1) offset1 += mv1.temp_component_eNum.get(cur_dim);
            }
        }
    }

    public void reciprocal_inplace(){
        for(int i = 0; i < this.total_eNum; i++){
            this._data[i] = 1.0 / this._data[i];
        }
    }

    public void inv_inplace(){
        for(int i = 0; i < this.total_eNum; i++){
            this._data[i] = -1.0 * this._data[i];
        }
    }

    public void sqrt_inplace(){
        for(int i = 0; i < this.total_eNum; i++){
            this._data[i] = Math.sqrt(this._data[i]);
        }
    }

    @Override
    public String toString() {
        return "MultiVector{" +
                "_shape=" + _shape.toString() +
                ", _data=" + Arrays.toString(_data) + "}";
    }

    /*-------------------------------------Static Methods---------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------*/



    public static MultiVector MultiVector_like(ArrayList<Integer> _shape, int initial_type)
    {
        int[] dims_size = new int[_shape.size()];
        for(int i = 0; i < _shape.size(); i++) dims_size[i] = _shape.get(i);
        return new MultiVector(dims_size, initial_type);
    }
    public static MultiVector MultiVector_like(MultiVector mv1, int initial_type)
    {
        int[] dims_size = new int[mv1._dims];
        for(int i = 0; i < mv1._dims; i++) dims_size[i] = mv1._shape.get(i);
        return new MultiVector(dims_size, initial_type);
    }
    public static MultiVector MultiVector_like(MultiVector mv1)
    {
        int[] dims_size = new int[mv1._dims];
        for(int i = 0; i < mv1._dims; i++) dims_size[i] = mv1._shape.get(i);
        return new MultiVector(dims_size);
    }

    public static MultiVector add(MultiVector mv1, double c)
    {
        MultiVector mv2 = new MultiVector(c);
        try{
            return _op_with_check(0, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static MultiVector add(MultiVector mv1, MultiVector mv2)
    {
        try{
            return _op_with_check(0, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void add(MultiVector mv1, double c, MultiVector res)
    {
        MultiVector mv2 = new MultiVector(c);
        if(res.needBroadcast){
            _op_recur(Calculation.ADD, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.ADD, mv1, mv2, res);
        }
    }
    public static void add(MultiVector mv1, MultiVector mv2, MultiVector res)
    {
        if(res.needBroadcast){
            _op_recur(Calculation.ADD, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.ADD, mv1, mv2, res);
        }
    }

    public static MultiVector sub(MultiVector mv1, double c)
    {
        MultiVector mv2 = new MultiVector(c);
        try{
            return _op_with_check(1, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static MultiVector sub(MultiVector mv1, MultiVector mv2)
    {
        try{
            return _op_with_check(1, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void sub(MultiVector mv1, double c, MultiVector res)
    {
        MultiVector mv2 = new MultiVector(c);
        if(res.needBroadcast){
            _op_recur(Calculation.SUB, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.SUB, mv1, mv2, res);
        }
    }
    public static void sub(MultiVector mv1, MultiVector mv2, MultiVector res)
    {
        if(res.needBroadcast){
            _op_recur(Calculation.SUB, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.SUB, mv1, mv2, res);
        }
    }

    public static MultiVector mul(MultiVector mv1, double c)
    {
        MultiVector mv2 = new MultiVector(c);
        try{
            return _op_with_check(2, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static MultiVector mul(MultiVector mv1, MultiVector mv2)
    {
        try{
            return _op_with_check(2, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void mul(MultiVector mv1, double c, MultiVector res)
    {
        MultiVector mv2 = new MultiVector(c);
        if(res.needBroadcast){
            _op_recur(Calculation.MUL, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.MUL, mv1, mv2, res);
        }
    }
    public static void mul(MultiVector mv1, MultiVector mv2, MultiVector res)
    {
        if(res.needBroadcast){
            _op_recur(Calculation.MUL, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.MUL, mv1, mv2, res);
        }
    }

    public static MultiVector div(MultiVector mv1, double c)
    {
        MultiVector mv2 = new MultiVector(c);
        try{
            return _op_with_check(3, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static MultiVector div(MultiVector mv1, MultiVector mv2)
    {
        try{
            return _op_with_check(3, mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void div(MultiVector mv1, double c, MultiVector res)
    {
        MultiVector mv2 = new MultiVector(c);
        if(res.needBroadcast){
            _op_recur(Calculation.DIV, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.DIV, mv1, mv2, res);
        }
    }
    public static void div(MultiVector mv1, MultiVector mv2, MultiVector res)
    {
        if(res.needBroadcast){
            _op_recur(Calculation.DIV, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        }
        else{
            _op_nonRecur(Calculation.DIV, mv1, mv2, res);
        }
    }

    public static ArrayList<Integer> _getOpResultShape(MultiVector mv1, MultiVector mv2) throws Exception {
        ArrayList<Integer> res_shape = new ArrayList<>();
        int sp1 = mv1._shape.size() - 1;
        int sp2 = mv2._shape.size() - 1;
        while(sp1 >= 0 && sp2 >= 0)
        {
            if(!mv1._shape.get(sp1).equals(mv2._shape.get(sp2)))
            {
                if(mv1._shape.get(sp1) != 1 && mv2._shape.get(sp2) != 1) throw new Exception(String.format("Dimension NOT match and can NOT broadcast! at dimension %s and dimension %s, got dimension size %s -- %s!", sp1, sp2, mv1._shape.get(sp1), mv2._shape.get(sp2)));
            }
            res_shape.add(Math.max(mv1._shape.get(sp1), mv2._shape.get(sp2)));
            sp1--;
            sp2--;
        }
        if(sp1 >= 0)
        {
            while(sp1 >= 0)
            {
                res_shape.add(mv1._shape.get(sp1));
                sp1--;
            }
        }
        else if(sp2 >= 0)
        {
            while(sp2 >= 0)
            {
                res_shape.add(mv2._shape.get(sp2));
                sp2--;
            }
        }

        if(res_shape.isEmpty()) throw new Exception("Why the hell the result is empty???");
        // reverse shape list
        Collections.reverse(res_shape);
        return res_shape;
    }

    public static MultiVector _op_with_check(int op, MultiVector mv1, MultiVector mv2) throws Exception
    {
        mv1.temp_shape.clear();
        mv1.temp_component_eNum.clear();
        mv2.temp_shape.clear();
        mv2.temp_component_eNum.clear();

        ArrayList<Integer> res_shape = new ArrayList<>();
        int sp1 = mv1._shape.size() - 1;
        int sp2 = mv2._shape.size() - 1;
        boolean needBoradcast = false;
        while(sp1 >= 0 && sp2 >= 0)
        {
            if(!mv1._shape.get(sp1).equals(mv2._shape.get(sp2)))
            {
                needBoradcast = true;
                if(mv1._shape.get(sp1) != 1 && mv2._shape.get(sp2) != 1){
                    throw new Exception(String.format("op=(%s), Dimension NOT match and can NOT broadcast! at dimension %d of operand-1 and dimension %d of operand-2, got dimension size %d -- %d!", Calculation.BiOpSign2String[op], sp1, sp2, mv1._shape.get(sp1), mv2._shape.get(sp2)));
                }
            }
            mv1.temp_shape.add(mv1._shape.get(sp1));
            mv2.temp_shape.add(mv2._shape.get(sp2));
            res_shape.add(Math.max(mv1._shape.get(sp1), mv2._shape.get(sp2)));
            sp1--;
            sp2--;
        }
        if(sp1 >= 0)
        {
            needBoradcast = true;
            while(sp1 >= 0)
            {
                res_shape.add(mv1._shape.get(sp1));
                mv1.temp_shape.add(mv1._shape.get(sp1));
                mv2.temp_shape.add(1);
                sp1--;
            }
        }
        else if(sp2 >= 0)
        {
            needBoradcast = true;
            while(sp2 >= 0)
            {
                res_shape.add(mv2._shape.get(sp2));
                mv2.temp_shape.add(mv2._shape.get(sp2));
                mv1.temp_shape.add(1);
                sp2--;
            }
        }

        if(res_shape.isEmpty()) throw new Exception("Why the hell the result is empty???");
        // reverse shape list
        Collections.reverse(mv1.temp_shape);
        Collections.reverse(mv2.temp_shape);
        Collections.reverse(res_shape);

        int[] dims_size = new int[res_shape.size()];
        for(int i = 0; i < res_shape.size(); i++) dims_size[i] = res_shape.get(i);
        MultiVector res = new MultiVector(dims_size, 0);
        res.needBroadcast = needBoradcast;

        if(!needBoradcast)
        {
            _op_nonRecur(op, mv1, mv2, res);
            return res;
        }
        //_component_eNum needs to change
        int tot1 = mv1.total_eNum;
        int tot2 = mv2.total_eNum;
        for(int i = 0; i < res_shape.size(); i++)
        {
            mv1.temp_component_eNum.add(tot1 / mv1.temp_shape.get(i));
            tot1 /= mv1.temp_shape.get(i);
            mv2.temp_component_eNum.add(tot2 / mv2.temp_shape.get(i));
            tot2 /= mv2.temp_shape.get(i);
        }
        /* recursive function*/
        _op_recur(op, mv1, mv2, res, 0, 0, 0, 0, 0, 0, 0);
        return res;
    }

    private static void _op_nonRecur(int op, MultiVector mv1, MultiVector mv2, MultiVector res) {
        switch(op)
        {
            case Calculation.ADD://add
            {
                for(int i = 0; i < res.total_eNum; i++)
                {
                    res._data[i] = mv1._data[i] + mv2._data[i];
                }
                break;
            }
            case Calculation.SUB://sub
            {
                for(int i = 0; i < res.total_eNum; i++)
                {
                    res._data[i] = mv1._data[i] - mv2._data[i];
                }
                break;
            }
            case Calculation.MUL://multiply
            {
                for(int i = 0; i < res.total_eNum; i++)
                {
                    res._data[i] = mv1._data[i] * mv2._data[i];
                }
                break;
            }
            case Calculation.DIV://divide
            {
                for(int i = 0; i < res.total_eNum; i++)
                {
                    res._data[i] = mv1._data[i] / mv2._data[i];
                }
                break;
            }
            default:
                throw new RuntimeException("How do you end up here???");
        }
    }

    public static void _op_recur(int op, MultiVector mv1, MultiVector mv2, MultiVector res, int cur_dim, int res_idx, int idx1, int idx2, int _res_offset, int _offset1, int _offset2)
    {
        if(cur_dim < res._dims)
        {
            if(cur_dim == res._dims - 1)
            {
                int res_ed = res._shape.get(cur_dim);
                int ed1 = mv1.temp_shape.get(cur_dim);
                int ed2 = mv2.temp_shape.get(cur_dim);
                switch(op)
                {
                    case 0:// add
                    {
                        for(int i = 0; i < res_ed; i++)
                        {
                            // System.out.println(mv1._data[Math.min(i, ed1 - 1) + _offset1] + "   " + mv2._data[Math.min(i, ed2 - 1) + _offset2]);
                            res._data[i + _res_offset] = mv1._data[Math.min(i, ed1 - 1) + _offset1] + mv2._data[Math.min(i, ed2 - 1) + _offset2];
                        }
                        break;
                    }
                    case 1:// sub
                    {
                        for(int i = 0; i < res_ed; i++)
                        {
                            res._data[i + _res_offset] = mv1._data[Math.min(i, ed1 - 1) + _offset1] - mv2._data[Math.min(i, ed2 - 1) + _offset2];
                        }
                        break;
                    }
                    case 2:// multiply
                    {
                        for(int i = 0; i < res_ed; i++)
                        {
                            res._data[i + _res_offset] = mv1._data[Math.min(i, ed1 - 1) + _offset1] * mv2._data[Math.min(i, ed2 - 1) + _offset2];
                        }
                        break;
                    }
                    case 3:// divide /* still needs to be checked */
                    {
                        for(int i = 0; i < res_ed; i++)
                        {
                            res._data[i + _res_offset] = mv1._data[Math.min(i, ed1 - 1) + _offset1] / mv2._data[Math.min(i, ed2 - 1) + _offset2];
                        }
                        break;
                    }
                    default:
                        throw new RuntimeException("How do you end up here???");
                }
            }
            else
            {
                int res_ed = res._shape.get(cur_dim);
                int res_offset = _res_offset;
                int offset1 = _offset1;
                int offset2 = _offset2;
                for(int i = 0; i < res_ed; i++)
                {
                    if(cur_dim + 1 < res._dims) _op_recur(op, mv1, mv2, res, cur_dim + 1, i, i, i, res_offset, offset1, offset2);
                    res_offset += res._component_eNum.get(cur_dim);
                    if(mv1.temp_shape.get(cur_dim) != 1) offset1 += mv1.temp_component_eNum.get(cur_dim);
                    if(mv2.temp_shape.get(cur_dim) != 1) offset2 += mv2.temp_component_eNum.get(cur_dim);
                }
            }
        }
    }
    public static MultiVector reciprocal(MultiVector mv1){
        MultiVector res = new MultiVector(mv1);
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = 1.0 / mv1._data[i];
        }
        return res;
    }
    public static MultiVector inv(MultiVector mv1){
        MultiVector res = new MultiVector(mv1);
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = -mv1._data[i];
        }
        return res;
    }
    public static MultiVector sqrt(MultiVector mv1){
        MultiVector res = new MultiVector(mv1);
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = Math.sqrt(mv1._data[i]);
        }
        return res;
    }

    public static MultiVector matmul(MultiVector mv1, MultiVector mv2)
    {
        MultiVector res = null;
        try{
            res = _matmul(mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public static void matmul(MultiVector mv1, MultiVector mv2, MultiVector res)
    {
        _matmul_recur(mv1, mv2, res, 0, 0, 0, 0);
    }
    public static MultiVector _matmul(MultiVector mv1, MultiVector mv2) throws Exception
    {
        mv1.temp_shape.clear();
        mv1.temp_component_eNum.clear();
        mv2.temp_shape.clear();
        mv2.temp_component_eNum.clear();

        if(mv1._dims < 2 || mv2._dims < 2) throw new Exception(String.format("When performing matrix multiplication, two MultiVectors should have at least 2 dimensions, while got %d and %d !", mv1._dims, mv2._dims));

        if(!mv1._shape.get(mv1._dims - 1).equals(mv2._shape.get(mv2._dims - 2)))
        {
            throw new Exception(String.format("When performing matrix multiplication, the last dimension size of the first MultiVector shuld match the second last dimension size of the second MultiVector, while got %d and %d !", mv1._shape.get(mv1._dims - 1), mv2._shape.get(mv2._dims - 2)));
        }

        mv1.temp_shape.add(mv1._shape.get(mv1._dims - 1));
        mv1.temp_shape.add(mv1._shape.get(mv1._dims - 2));
        mv2.temp_shape.add(mv2._shape.get(mv2._dims - 1));
        mv2.temp_shape.add(mv2._shape.get(mv2._dims - 2));

        ArrayList<Integer> res_shape = new ArrayList<>();
        res_shape.add(mv2._shape.get(mv2._dims - 1));
        res_shape.add(mv1._shape.get(mv1._dims - 2));

        int sp1 = mv1._shape.size() - 3;
        int sp2 = mv2._shape.size() - 3;
        boolean needBoradcast = false;
        while(sp1 >= 0 && sp2 >= 0)
        {
            if(!mv1._shape.get(sp1).equals(mv2._shape.get(sp2)))
            {
                needBoradcast = true;
                if(mv1._shape.get(sp1) != 1 && mv2._shape.get(sp2) != 1)
                {
                    String dims_str1 = "(";
                    String dims_str2 = "(";
                    for(int i = 0; i < mv1._dims; i++)
                    {
                        if(i < mv1._dims - 2) dims_str1 += String.valueOf(mv1._shape.get(i));
                        else dims_str1 += "*";
                        if(i < mv1._dims - 1) dims_str1 += ", ";
                        else dims_str1 += ")";
                    }
                    for(int i = 0; i < mv2._dims; i++)
                    {
                        if(i < mv2._dims - 2) dims_str2 += String.valueOf(mv2._shape.get(i));
                        else dims_str2 += "*";
                        if(i < mv2._dims - 1) dims_str2 += ", ";
                        else dims_str2 += ")";
                    }
                    String format_str = "Dimension NOT match and can NOT broadcast between" + dims_str1  + " and " + dims_str2  + ", at dimension %s and dimension %s, got dimension size %s -- %s!";
                    throw new Exception(String.format(format_str, sp1, sp2, mv1._shape.get(sp1), mv2._shape.get(sp2)));
                }
            }
            mv1.temp_shape.add(mv1._shape.get(sp1));
            mv2.temp_shape.add(mv2._shape.get(sp2));
            res_shape.add(Math.max(mv1._shape.get(sp1), mv2._shape.get(sp2)));
            sp1--;
            sp2--;
        }
        if(sp1 >= 0)
        {
            needBoradcast = true;
            while(sp1 >= 0)
            {
                res_shape.add(mv1._shape.get(sp1));
                mv1.temp_shape.add(mv1._shape.get(sp1));
                mv2.temp_shape.add(1);
                sp1--;
            }
        }
        else if(sp2 >= 0)
        {
            needBoradcast = true;
            while(sp2 >= 0)
            {
                res_shape.add(mv2._shape.get(sp2));
                mv2.temp_shape.add(mv2._shape.get(sp2));
                mv1.temp_shape.add(1);
                sp2--;
            }
        }

        if(res_shape.isEmpty()) throw new Exception("Why the hell the result is empty???");
        // reverse shape list
        Collections.reverse(mv1.temp_shape);
        Collections.reverse(mv2.temp_shape);
        Collections.reverse(res_shape);

        int[] dims_size = new int[res_shape.size()];
        for(int i = 0; i < res_shape.size(); i++) dims_size[i] = res_shape.get(i);
        MultiVector res = new MultiVector(dims_size, 0);
        res.needBroadcast = needBoradcast;
        //_component_eNum needs to change
        int tot1 = mv1.total_eNum;
        int tot2 = mv2.total_eNum;
        for(int i = 0; i < res_shape.size(); i++)
        {
            mv1.temp_component_eNum.add(tot1 / mv1.temp_shape.get(i));
            tot1 /= mv1.temp_shape.get(i);
            mv2.temp_component_eNum.add(tot2 / mv2.temp_shape.get(i));
            tot2 /= mv2.temp_shape.get(i);
        }
        _matmul_recur(mv1, mv2, res, 0, 0, 0, 0);
        return res;
    }
    public static void _matmul_recur(MultiVector mv1, MultiVector mv2, MultiVector res, int cur_dim, int _res_offset, int _offset1, int _offset2)
    {
        if(cur_dim == res._dims - 2)
        {
            int res_row_ed = res._shape.get(cur_dim);
            int res_col_ed = res._shape.get(cur_dim + 1);
            // int row_ed1 = mv1.temp_shape.get(cur_dim);
            int col_ed1 = mv1.temp_shape.get(cur_dim + 1);
            // int row_ed2 = mv2.temp_shape.get(cur_dim);
            int col_ed2 = mv2.temp_shape.get(cur_dim + 1);
            for(int i = 0; i < res_row_ed; i++)
            {
                for(int j = 0; j < res_col_ed; j++)
                {
                    double _val = 0.0;
                    // String str = String.valueOf(i) + "," + String.valueOf(j) + "---(";
                    for(int k = 0; k < col_ed1; k++)
                    {
                        _val += mv1._data[_offset1 + i * col_ed1 + k] * mv2._data[_offset2 + k * col_ed2 + j];
                        // str += String.valueOf(mv1._data[_offset1 + i * col_ed1 + k]) + " * " + String.valueOf(mv2._data[_offset2 + k * col_ed2 + j]);
                        // if(k < col_ed1 - 1) str += ", ";
                        // else str += ")";
                    }
                    // System.out.println(str);
                    res._data[_res_offset + i * res_col_ed + j] = _val;
                }
            }
        }
        else
        {
            int res_ed = res._shape.get(cur_dim);
            int res_offset = _res_offset;
            int offset1 = _offset1;
            int offset2 = _offset2;
            for(int i = 0; i < res_ed; i++)
            {
                _matmul_recur(mv1, mv2, res, cur_dim + 1,res_offset, offset1, offset2);
                if(res._shape.get(cur_dim) != 1) res_offset += res._component_eNum.get(cur_dim);
                if(mv1.temp_shape.get(cur_dim) != 1) offset1 += mv1.temp_component_eNum.get(cur_dim);
                if(mv2.temp_shape.get(cur_dim) != 1) offset2 += mv2.temp_component_eNum.get(cur_dim);
            }
        }
    }

    public static MultiVector dot(MultiVector mv1, MultiVector mv2)
    {
        MultiVector res = null;
        try{
            res = _dot(mv1, mv2);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public static MultiVector _dot(MultiVector mv1, MultiVector mv2) throws Exception
    {
        mv1.temp_shape.clear();
        mv1.temp_component_eNum.clear();
        mv2.temp_shape.clear();
        mv2.temp_component_eNum.clear();

        if(mv1._dims < 1 || mv2._dims < 2) throw new Exception(String.format("When performing dot multiplication, the first should have at least 1 dimensions, the second should have at least 2 dimensions, while got %d and %d !", mv1._dims, mv2._dims));

        if(!mv1._shape.get(mv1._dims - 1).equals(mv2._shape.get(mv2._dims - 2)))
        {
            StringBuilder dims_str1 = new StringBuilder("(");
            StringBuilder dims_str2 = new StringBuilder("(");
            for(int i = 0; i < mv1._dims; i++)
            {
                if(i < mv1._dims - 2) dims_str1.append(mv1._shape.get(i));
                else dims_str1.append("*");

                if(i < mv1._dims - 1) dims_str1.append(", ");
                else dims_str1.append(")");
            }
            for(int i = 0; i < mv2._dims; i++)
            {
                if(i < mv2._dims - 2) dims_str2.append(mv2._shape.get(i));
                else dims_str2.append("*");

                if(i < mv2._dims - 1) dims_str2.append(", ");
                else dims_str2.append(")");
            }
            String format_str = "When performing dot multiplication, the last dimension size of the first MultiVector shuld match the second last dimension size of the second MultiVector, while " + dims_str1  + " and " + dims_str2  + " are NOT aligned: %d (dim %d) != %d (dim %d)";
            throw new Exception(String.format(format_str, mv1._shape.get(mv1._dims - 1), mv1._dims - 1, mv2._shape.get(mv2._dims - 2), mv1._dims - 2));

        }

        mv1.temp_shape.add(mv1._shape.get(mv1._dims - 1));

        mv2.temp_shape.add(mv2._shape.get(mv2._dims - 1));
        mv2.temp_shape.add(mv2._shape.get(mv2._dims - 2));

        ArrayList<Integer> res_shape = new ArrayList<>();
        res_shape.add(mv2._shape.get(mv2._dims - 1));


        int sp1 = mv1._shape.size() - 2;
        int sp2 = mv2._shape.size() - 3;
        while(sp2 >= 0)
        {
            // mv1.temp_shape.add(mv1._shape.get(sp1));
            mv2.temp_shape.add(mv2._shape.get(sp2));
            res_shape.add(mv2._shape.get(sp2));
            sp2--;
        }
        while(sp1 >= 0)
        {
            // mv1.temp_shape.add(mv1._shape.get(sp1));
            mv1.temp_shape.add(mv1._shape.get(sp1));
            res_shape.add(mv1._shape.get(sp1));
            sp1--;
        }

        if(res_shape.isEmpty()) throw new Exception("Why the hell the result is empty???");
        // reverse shape list
        Collections.reverse(mv1.temp_shape);
        Collections.reverse(mv2.temp_shape);
        Collections.reverse(res_shape);

        int[] dims_size = new int[res_shape.size()];
        for(int i = 0; i < res_shape.size(); i++) dims_size[i] = res_shape.get(i);
        MultiVector res = new MultiVector(dims_size, 0);

        //_component_eNum needs to change
        int tot1 = mv1.total_eNum;
        int tot2 = mv2.total_eNum;
        for(int i = 0; i < mv1.temp_shape.size(); i++)
        {
            mv1.temp_component_eNum.add(tot1 / mv1.temp_shape.get(i));
            tot1 /= mv1.temp_shape.get(i);
        }
        for(int i = 0; i < mv2.temp_shape.size(); i++)
        {
            mv2.temp_component_eNum.add(tot2 / mv2.temp_shape.get(i));
            tot2 /= mv2.temp_shape.get(i);
        }
        _dot_recur(mv1, mv2, res, 0, 0, 0, 0);
        return res;
    }
    public static void _dot_recur(MultiVector mv1, MultiVector mv2, MultiVector res, int cur_dim, int _res_offset, int _offset1, int _offset2)
    {
        if(cur_dim == res._dims - 1)
        {
            int res_ed = res._shape.get(cur_dim);
            // int row_ed1 = mv1.temp_shape.get(cur_dim);
            int ed1 = mv1.temp_shape.get(mv1._dims - 1);

            int col_ed2 = mv2.temp_shape.get(mv2._dims - 1);
            for(int i = 0; i < res_ed; i++)
            {
                double _val = 0.0;
                for(int k = 0; k < ed1; k++)
                {
                    _val += mv1._data[_offset1 + k] * mv2._data[_offset2 + k * col_ed2 + i];
                }
                res._data[_res_offset + i] = _val;
            }
        }
        else
        {
            int res_ed = res._shape.get(cur_dim);
            int res_offset = _res_offset;
            int offset1 = _offset1;
            int offset2 = _offset2;
            int dimTopofFirst = mv1._dims - 1;
            for(int i = 0; i < res_ed; i++)
            {
                _dot_recur(mv1, mv2, res, cur_dim + 1, res_offset, offset1, offset2);
                if(res._shape.get(cur_dim) != 1) res_offset += res._component_eNum.get(cur_dim);

                if(cur_dim < dimTopofFirst && mv1.temp_shape.get(cur_dim) != 1) offset1 += mv1.temp_component_eNum.get(cur_dim);
                else if(mv2.temp_shape.get(cur_dim - dimTopofFirst) != 1) offset2 += mv2.temp_component_eNum.get(cur_dim - dimTopofFirst);
            }
        }
    }





    public static MultiVector sum(MultiVector mv1, boolean retain_shape, int...axes)
    {
        try{
            return _sum_with_check(mv1, retain_shape, axes);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void sum(MultiVector res, MultiVector mv1, boolean retain_shape, int...axes)
    {
        res.set_zeros();
        if(axes.length == 0){
            _sum_nonRecur(res, mv1);
        }
        else _sum_recur(res, mv1, 0, 0, 0, 0, 0);
        if(!retain_shape) res.squeeze();
    }
    public static MultiVector _sum_with_check(MultiVector mv1, boolean retain_shape, int...axes) throws Exception
    {
        int axis_upBound = mv1._shape.size();
        int[] res_shape = new int[axis_upBound];
        MultiVector res;
        if(axes.length == 0)
        {
            //sum all of them
            for(int i = 0; i < mv1._shape.size(); i++) res_shape[i] = 1;
            res = new MultiVector(res_shape, 0);
            _sum_nonRecur(res, mv1);
        }
        else
        {
            //sum recursively
            for(int i = 0; i < mv1._shape.size(); i++) res_shape[i] = mv1._shape.get(i);
            for(int axis : axes)
            {
                if(axis > axis_upBound)
                {
                    throw new Exception(String.format("Axis %d is Non-existent, where top axis is %d", axis, axis_upBound - 1));
                }
                res_shape[axis] = 1;
            }
            res = new MultiVector(res_shape, 0);
            _sum_recur(res, mv1, 0, 0, 0, 0, 0);
        }
        if(!retain_shape) res.squeeze();
        return res;
    }
    public static void _sum_nonRecur(MultiVector res, MultiVector mv1){
        for(int i = 0; i < mv1.total_eNum; i++)
        {
            res._data[0] += mv1._data[i];
        }
    }
    public static void _sum_recur(MultiVector res, MultiVector mv1, int cur_dim, int res_idx, int idx1, int _res_offset, int _offset1)
    {
        if(cur_dim < res._dims)
        {
            if(cur_dim == res._dims - 1)
            {
                int res_ed = res._shape.get(cur_dim);
                int mv1_ed = mv1._shape.get(cur_dim);
                /*The difference here between basic op is that:
                  mv1_ed was used in for(), not res_ed! Since we can assert that res_ed <= mv1_ed, no matter what */
                for(int i = 0; i < mv1_ed; i++)
                {
                    res._data[Math.min(i, res_ed - 1) + _res_offset] += mv1._data[i + _offset1];
                }
            }
            else
            {
                // int res_ed = res._shape.get(cur_dim);
                int mv1_ed = mv1._shape.get(cur_dim);
                int res_offset = _res_offset;
                int offset1 = _offset1;
                for(int i = 0; i < mv1_ed; i++)
                {
                    if(cur_dim + 1 < mv1._dims) _sum_recur(res, mv1, cur_dim + 1, i, i, res_offset, offset1);
                    offset1 += mv1._component_eNum.get(cur_dim);
                    if(res._shape.get(cur_dim) != 1) res_offset += res._component_eNum.get(cur_dim);
                }
            }
        }
    }


    public static MultiVector max(MultiVector mv1, HashMap<Integer, Integer> markers, boolean retain_shape, int...axes){
        try{
            return _max_with_check(mv1, markers, retain_shape, axes);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void max(MultiVector res, MultiVector mv1, HashMap<Integer, Integer> markers, boolean retain_shape, int...axes){
        if(axes.length == 0){
            _max_nonRecur(res, mv1, markers);
        }
        else _max_recur(res, mv1, markers, 0,0,0,0,0);
        if(!retain_shape) res.squeeze();
    }


    private static MultiVector _max_with_check(MultiVector mv1, HashMap<Integer, Integer> markers, boolean retain_shape, int[] axes) throws Exception{
        int axis_upBound = mv1._shape.size();
        int[] res_shape = new int[axis_upBound];
        MultiVector res;
        if(axes.length == 0)
        {
            //return the max value of all elements!
            for(int i = 0; i < mv1._shape.size(); i++) res_shape[i] = 1;
            res = new MultiVector(res_shape, 0);
            if(markers == null){
                markers = new HashMap<>();
            }
            _max_nonRecur(res, mv1, markers);
        }
        else{
            //get max recursively
            for(int i = 0; i < mv1._shape.size(); i++) res_shape[i] = mv1._shape.get(i);
            for(int axis : axes)
            {
                if(axis > axis_upBound)
                {
                    throw new Exception(String.format("Axis %d is Non-existent, where top axis is %d", axis, axis_upBound - 1));
                }
                res_shape[axis] = 1;
            }
            res = new MultiVector(res_shape, Calculation.SET_DOUBLE_MIN);
            _max_recur(res, mv1, markers, 0,0,0,0,0);

        }
        if(!retain_shape) res.squeeze();
        return res;
    }
    public static void _max_nonRecur(MultiVector res, MultiVector mv1, HashMap<Integer, Integer> markers) {
        res._data[0] = Double.MIN_VALUE;
        for(int i = 0; i < mv1.total_eNum; i++)
        {
            if(mv1._data[i] > res._data[0]){
                res._data[0] = mv1._data[i];
                markers.put(0, i);
            }
        }
    }
    public static void _max_recur(MultiVector res, MultiVector mv1, HashMap<Integer, Integer> markers, int cur_dim, int res_idx, int idx1, int _res_offset, int _offset1){
        if(cur_dim < res._dims)
        {
            if(cur_dim == res._dims - 1)
            {
                int res_ed = res._shape.get(cur_dim);
                int mv1_ed = mv1._shape.get(cur_dim);
                for(int i = 0; i < mv1_ed; i++)
                {
                    int resIdx = Math.min(i, res_ed - 1) + _res_offset;
                    if(res._data[resIdx] < mv1._data[i + _offset1]){
                        res._data[resIdx] = mv1._data[i + _offset1];
                        markers.put(resIdx, i + _offset1);
                    }
                }
            }
            else
            {
                // int res_ed = res._shape.get(cur_dim);
                int mv1_ed = mv1._shape.get(cur_dim);
                int res_offset = _res_offset;
                int offset1 = _offset1;
                for(int i = 0; i < mv1_ed; i++)
                {
                    if(cur_dim + 1 < mv1._dims) _max_recur(res, mv1, markers, cur_dim + 1, i, i, res_offset, offset1);
                    offset1 += mv1._component_eNum.get(cur_dim);
                    if(res._shape.get(cur_dim) != 1) res_offset += res._component_eNum.get(cur_dim);
                }
            }
        }
    }


    public static MultiVector T(MultiVector mv1)
    {
        int[] axes = new int[mv1._shape.size()];
        int axesupBound = mv1._dims;
        for(int i = 0; i < axesupBound; i++)
        {
            axes[i] = axesupBound - i - 1;
        }
        return transpose(mv1, axes);
    }

    public static MultiVector transpose(MultiVector mv1, int...axes)
    {
        try{
            return _transpose(mv1, axes);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static void transpose(MultiVector res, MultiVector mv1, int...axes)
    {
        int[] mv1_axes_idx = new int[mv1._dims];
        _transpose_recur(res, mv1, axes,0, 0, mv1_axes_idx);
    }
    public static MultiVector _transpose(MultiVector mv1, int...axes) throws Exception
    {
        TreeSet<Integer> ts_axes = new TreeSet<>();
        for(int ax : axes) ts_axes.add(ax);
        if(axes.length != mv1._dims || ts_axes.size() != mv1._dims) throw new Exception("The length of axes is not equal to the shape of the input multivector!");
        int counter = 0;
        for(int ax : ts_axes)
        {
            if(counter != ax) throw new Exception("Axes should be a permutation of the the input multivector's axes!");
            counter++;
        }
        int axesupBound = mv1._dims;
        int[] dims_size = new int[axesupBound];
        for(int i = 0; i < axesupBound; i++)
        {
            dims_size[i] = mv1._shape.get(axes[i]);
        }
        MultiVector res = new MultiVector(dims_size, 0);
        if(res._dims == 1)
        {
            res._data = Arrays.copyOf(mv1._data, mv1.total_eNum);
            return res;
        }
        int[] mv1_axes_idx = new int[mv1._dims];
        _transpose_recur(res, mv1, axes,0, 0, mv1_axes_idx);
        return res;
    }
    public static void _transpose_recur(MultiVector res, MultiVector mv1, int[] axes, int cur_dim, int _res_offset, int[] mv1_axes_idx)
    {
        if(cur_dim == res._dims - 1)
        {
            int res_ed = res._shape.get(cur_dim);
            for(int i = 0; i < res_ed; i++)
            {
                mv1_axes_idx[axes[cur_dim]] = i;
                res._data[i + _res_offset] = getElement(mv1, mv1_axes_idx);
            }
        }
        else
        {
            int res_ed = res._shape.get(cur_dim);
            int res_offset = _res_offset;
            for(int i = 0; i < res_ed; i++)
            {
                mv1_axes_idx[axes[cur_dim]] = i;
                _transpose_recur(res, mv1, axes, cur_dim + 1, res_offset, mv1_axes_idx);
                if(res._shape.get(cur_dim) != 1) res_offset += res._component_eNum.get(cur_dim);
            }
        }
    }

    public static MultiVector squeeze(MultiVector mv1)
    {
        ArrayList<Integer> squeeze_axes_list = new ArrayList<>();
        for(int i = 0; i < mv1._dims; i++)
        {
            if(mv1._shape.get(i) == 1) squeeze_axes_list.add(i);
        }
        int[] squeeze_axes = new int[squeeze_axes_list.size()];
        for(int i = 0; i < squeeze_axes_list.size(); i++)
        {
            squeeze_axes[i] = squeeze_axes_list.get(i);
        }
        return MultiVector.squeeze(mv1, squeeze_axes);
    }
    public static MultiVector squeeze(MultiVector mv1, int...axes)
    {
        MultiVector res = null;
        try{
            res = MultiVector._squeeze(mv1, axes);
            return res;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    private static MultiVector _squeeze(MultiVector mv1, int...axes) throws Exception
    {
        ArrayList<Integer> new_shape = new ArrayList<>();
        HashSet<Integer> hashst = new HashSet<>();
        //check
        for (int axe : axes) {
            hashst.add(axe);
            if (axe < 0 || axe >= mv1._dims) {
                throw new Exception(String.format("Axis index % d out of bound !", axe));
            } else if (mv1._shape.get(axe) != 1) {
                throw new Exception(String.format("Axis size %d(at axis %d) != 1 !", mv1._shape.get(axe), axe));
            }
        }
        for(int i = 0; i < mv1._dims; i++)
        {
            if(!hashst.contains(i))
            {
                new_shape.add(mv1._shape.get(i));
            }
        }
        if(new_shape.isEmpty()) new_shape.add(1);
        MultiVector res = MultiVector.MultiVector_like(new_shape, 0);
        //assign data value
        res._data = Arrays.copyOf(mv1._data, mv1.total_eNum);
        return res;
    }

    public static MultiVector unsqueeze(MultiVector mv1, int axis)
    {
        MultiVector res = null;
        try{
            res = MultiVector._unsqueeze(mv1, axis);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    public static MultiVector _unsqueeze(MultiVector mv1, int axis) throws Exception
    {
        // if(axes.length == 0) throw new Exception("Must designate an axis position when using unsqueeze on a MultiVector!");
        if(axis < 0 || axis > mv1._dims)
        {
            throw new Exception(String.format("Axis index %d is out of range, should be in [0, %d]", axis, mv1._dims));
        }
        MultiVector res = MultiVector_like(mv1, 0);
        if(axis < mv1._dims)
        {
            res._shape.add(axis, 1);
            res._component_eNum.add(axis, mv1._component_eNum.get(axis));
        }
        else
        {
            res._shape.add(1);
            res._component_eNum.add(1);
        }
        res._dims += 1;
        if (mv1.total_eNum >= 0) System.arraycopy(mv1._data, 0, res._data, 0, mv1.total_eNum);
        return res;
    }

    public static MultiVector slice(MultiVector mv1, boolean retain_shape, int...idxes)
    {
        Pair<Integer, Integer>[] idxes_range = new Pair[idxes.length];
        for(int i = 0; i < idxes.length; i++)
        {
            idxes_range[i] = new Pair<>(idxes[i], idxes[i] + 1);
            System.out.println(idxes_range[i].first + "  " + idxes_range[i].second);
        }
        return slice(mv1, retain_shape, idxes_range);
    }
    public static MultiVector slice(MultiVector mv1, boolean retain_shape, Pair<Integer, Integer>...idxes_range)
    {
        MultiVector res = null;
        try{
            res = _slice(mv1, retain_shape, idxes_range);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }
    private static MultiVector _slice(MultiVector mv1, boolean retain_shape, Pair<Integer, Integer>..._idxes_range) throws Exception
    {
        Pair<Integer, Integer>[] idxes_range = new Pair[mv1._dims];
        int[] idxes_start = new int[mv1._dims];
        if(_idxes_range.length > mv1._dims) throw new Exception("Axes size out of range, Can NOT slice!");
        for(int i = 0; i < mv1._dims; i++)
        {
            if(i < _idxes_range.length)
            {
                int bg = _idxes_range[i].first;
                int ed = _idxes_range[i].second;
                int upBound = mv1._shape.get(i);
                if(bg >= ed || bg < 0 || bg >= upBound || ed > upBound)
                {
                    throw new Exception(String.format("Axes index Error! To get [start, end), the start index must in range [0, %d], end index must in range [1, %d], and begin index should be smaller than end index, while got %d and %d",upBound - 1, upBound, bg, ed));
                }
                idxes_range[i] = new Pair<>(bg, ed);

                idxes_start[i] = bg;
            }
            else
            {
                idxes_range[i] = new Pair<>(0, mv1._shape.get(i));

                idxes_start[i] = 0;
            }
        }
        int[] dims_size = new int[mv1._dims];
        for(int i = 0; i < mv1._dims; i++)
        {
            dims_size[i] = idxes_range[i].second - idxes_range[i].first;
        }
        MultiVector res = new MultiVector(dims_size, 0);
        _slice_recur(res, mv1, 0, 0, 0, idxes_start);

        if(!retain_shape) MultiVector.squeeze(res);
        return res;
    }
    private static void _slice_recur(MultiVector res, MultiVector mv1, int cur_dim, int _res_offset, int _offset1, int[] idxes_start)
    {
        if(cur_dim == res._dims - 1)
        {
            int res_ed = res._shape.get(cur_dim);
            for(int i = 0; i < res_ed; i++)
            {
                res._data[i + _res_offset] = mv1._data[i + _offset1];
            }
        }
        else
        {
            int res_ed = res._shape.get(cur_dim);
            int res_offset = _res_offset;
            int offset1 = _offset1 + mv1._component_eNum.get(cur_dim) * idxes_start[cur_dim];
            for(int i = 0; i < res_ed; i++)
            {
                _slice_recur(res, mv1, cur_dim + 1, res_offset, offset1, idxes_start);
                res_offset += res._component_eNum.get(cur_dim);
                offset1 += mv1._component_eNum.get(cur_dim);
            }
        }
    }

    public static double getElement(MultiVector mv, int...axes_idx)
    {
        try{
            return _getElement(mv, axes_idx);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0.0;
    }
    private static double _getElement(MultiVector mv, int...axes_idx) throws Exception
    {
        if(axes_idx.length != mv._shape.size())
        {
            throw new Exception("Dimision index size is NOT equal to dimision size!");
        }
        int offset = 0;
        for(int i = 0; i < axes_idx.length; i++)
        {
            if(axes_idx[i] >= mv._shape.get(i))
            {
                throw new Exception(String.format("Index %d is out of bounds for axis %d with size %d", axes_idx[i], i, mv._shape.get(i)));
            }
            offset += axes_idx[i] * mv._component_eNum.get(i);
        }
        return mv._data[offset];
    }

    public static ArrayList<Integer> getMatmulBroadcastInfo(MultiVector mv1, MultiVector res)
    {
        ArrayList<Integer> bcinfo = null;
        try{
            bcinfo = _getMatmulBroadcastInfo(mv1, res);
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return bcinfo;
    }
    private static ArrayList<Integer> _getMatmulBroadcastInfo(MultiVector mv1, MultiVector res) throws Exception
    {
        if(mv1._shape.size() < 2 || mv1._shape.size() > res._shape.size())
        {
            throw new Exception(String.format("When getting Matmul Broadcast info, the first should have at least 2 dimensions, and second(result) should have more or equal dimensions, while got %d dimensions and %d dimensions!", mv1._shape.size(), res._shape.size()));
        }
        if(!mv1._shape.get(mv1._dims - 1).equals(res._shape.get(res._dims - 1)) && !mv1._shape.get(mv1._dims - 2).equals(res._shape.get(res._dims - 2)))
        {
            StringBuilder dims_str1 = new StringBuilder("(");
            StringBuilder dims_strres = new StringBuilder("(");
            for(int i = 0; i < mv1._dims; i++)
            {
                dims_str1.append(mv1._shape.get(i));
                if(i < mv1._dims - 1) dims_str1.append(", ");
                else dims_str1.append(")");
            }
            for(int i = 0; i < res._dims; i++)
            {
                dims_strres.append(res._shape.get(i));
                if(i < res._dims - 1) dims_strres.append(", ");
                else dims_strres.append(")");
            }
            String format_str = "MultiVector with shape " + dims_str1  + " is NOT a matmul operand of MultiVector with shape " + dims_strres;
            throw new Exception(format_str);
        }
        ArrayList<Integer> broadcastInfo = new ArrayList<>();
        broadcastInfo.add(0);
        broadcastInfo.add(0);
        int spres = res._dims - 3;
        int sp1 = mv1._dims - 3;
        while(sp1 >= 0)
        {
            if(res._shape.get(spres).equals(mv1._shape.get(sp1))) broadcastInfo.add(0);
            else
            {
                if(mv1._shape.get(sp1) != 1)
                {
                    StringBuilder dims_str1 = new StringBuilder("(");
                    StringBuilder dims_strres = new StringBuilder("(");
                    for(int i = 0; i < mv1._dims; i++)
                    {
                        dims_str1.append(mv1._shape.get(i));
                        if(i < mv1._dims - 1) dims_str1.append(", ");
                        else dims_str1.append(")");
                    }
                    for(int i = 0; i < res._dims; i++)
                    {
                        dims_strres.append(res._shape.get(i));
                        if(i < res._dims - 1) dims_strres.append(", ");
                        else dims_strres.append(")");
                    }
                    String format_str = "MultiVector with shape " + dims_str1  + " is NOT a matmul operand of MultiVector with shape " + dims_strres;
                    throw new Exception(format_str);
                }
                else broadcastInfo.add(1);
            }
            sp1--;
            spres--;
        }
        while(spres >= 0)
        {
            broadcastInfo.add(-1);
            spres--;
        }
        Collections.reverse(broadcastInfo);
        return broadcastInfo;
    }

    public static ArrayList<Integer> getOpBroadcastInfo(MultiVector mv1, MultiVector res){
        ArrayList<Integer> broadcastInfo = new ArrayList<>();
        int axres = res._dims - 1;
        int ax1 = mv1._dims - 1;
        while(ax1 >= 0){
            if(res._shape.get(axres).equals(mv1._shape.get(ax1))){
                broadcastInfo.add(0);
            }
            else{
                broadcastInfo.add(1);
            }
            ax1--;
            axres--;
        }
        while (axres >= 0){
            broadcastInfo.add(-1);
            axres--;
        }
        Collections.reverse(broadcastInfo);
        return broadcastInfo;
    }



    /*-------------------------------------Function Methods---------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------
    --------------------------------------------------------------------------------------*/

    //exp(x)
    public static MultiVector exp(MultiVector mv1){
        MultiVector res = MultiVector.MultiVector_like(mv1, Calculation.SET_ALL_ZEROS);
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = Math.exp(mv1._data[i]);
        }
        return res;
    }
    public static void exp(MultiVector mv1, MultiVector res){
        res.set_zeros();
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = Math.exp(mv1._data[i]);
        }
    }

    //log(x)
    public static MultiVector log(MultiVector mv1){
        MultiVector res = MultiVector.MultiVector_like(mv1, Calculation.SET_ALL_ZEROS);
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = Math.log(mv1._data[i]);
        }
        return res;
    }
    public static void log(MultiVector mv1, MultiVector res){
        res.set_zeros();
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = Math.log(mv1._data[i]);
        }
    }

    //sigmoid(x)
    public static MultiVector sigmoid(MultiVector mv1){
        MultiVector res = MultiVector.MultiVector_like(mv1, Calculation.SET_ALL_ZEROS);
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = 1.0 / (1.0 + Math.exp(-1.0 * mv1._data[i]));
        }
        return res;
    }
    public static void sigmoid(MultiVector mv1, MultiVector res){
        res.set_zeros();
        for(int i = 0; i < res.total_eNum; i++){
            res._data[i] = 1.0 / (1.0 + Math.exp(-1.0 * mv1._data[i]));
        }
    }
}




