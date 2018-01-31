### 界面维护-接口定义

接口输入输出内容：

1.查询接口 ： （输入 关键字，时间  由后端 给返回数据，方便前端做分页）

	输入：json 格式； 
		{
		keyword ：String 类型 “关键字” 
		time ：Int 类型，时间戳 ，精确到毫秒
		
		}
	输出：json 格式；
		{
		是否有数据：1或0,（1代表有数据，0代表没有数据）
      		Count : Int  总行数
			{  
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
				...
				...
				...				
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}

				}
		}		

2.侧边栏接口： （ 进入页面就显示一级菜单，点击一级菜单，给后端发送一级菜单的rowkey，然后后端返回二级菜单的数据）

	输入：json 格式； 
		{
		rowkey ：Int 类型 行业编码 
		
		
		}
	输出：json 格式；
		{
		是否有数据：1或0,（1代表有数据，0代表没有数据）
      		
			{  
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
				...
				...
				...				
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}

				}
		}

3.添加同级菜单：:  （添加一级文件(目前库里没有),添加二级文件 :  前端把 新rowkey 和信息发到后端 更新到数据库,然后 返回 新的二级文件目录）


	输入：json 格式； 
		{
		rowkey ：String ，
		family ：String ，
		column ：String ，
		value  ：String
		}
	输出：json 格式；
		{
		是否有数据：1或0,（1代表有数据，0代表没有数据）
      		
			{  
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
				...
				...
				...				
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}

				}
		}
4.删除：（把要删除的数据信息发给后端，后端删除后，返回新的菜单数据）

	输入：json 格式； 
		{
		rowkey ：String ，
		family ：String ，
		column ：String ，
		value  ：String
		}
	输出：json 格式；
		{
		是否有数据：1或0,（1代表有数据，0代表没有数据）
      		
			{  
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
				...
				...
				...				
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}

				}
		}

5.重命名：（把需要重命名的原始信息和新名字发到后端，后端进行删除，添加，动作 然后再返回个新的菜单数据）

	输入：json 格式； 
		{
		rowkey ：String ，
		family ：String ，
		column ：String ，
		value  ：String ，
		newcolumn ：String
		}
	输出：json 格式；
		{
		是否有数据：1或0,（1代表有数据，0代表没有数据）
      		
			{  
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
				...
				...
				...				
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}

				}
		}

6.添加子菜单 ： （ 前端把 新rowkey 和信息发到后端 更新到数据库,然后 返回 新的子级文件目录）

	输入：json 格式； 
		{
		rowkey ：String ，
		family ：String ，
		column ：String ，
		value  ：String
		}
	输出：json 格式；
		{
		是否有数据：1或0,（1代表有数据，0代表没有数据）
      		
			{  
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}，
				...
				...
				...				
			{rowkey ：String ，family ：String ，column ：String ，value  ：String}

				}
		}

*注意： 对于 添加同级菜单，和添加子菜单 ， 如果前端 给rowkey 比较麻烦， 可以约定 
添加同级时 ：一级菜单，rowkey ：1，二级菜单，rowkey ：2， 以此类推
添加子菜单时 ： 一级菜单的子菜单 ，rowkey ：2 ，二级菜单的子菜单， rowkey：3 以此类推
然后 后端自行判断 要添加的是 哪一级菜单，然后 倒叙查询 取第一个rowkey +1 组成新菜单的rowkey




