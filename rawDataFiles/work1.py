# -*- coding: utf-8 -*-
# 这是一个用于合并同一个文件夹内所有CSV文件的通用脚本。
# 假设：所有CSV文件都拥有相同的列结构和列标题。

import os
import glob
import pandas as pd

# --- 第一步: 用户配置 (请在这里修改) ---

# 请输入包含所有CSV文件的文件夹路径。
# 示例 (Windows): 'E:\\高考志愿填报助手\\rawDataFiles\\一分一段\\待合并'
# 示例 (Mac/Linux): '/Users/yourname/Documents/data'
input_folder_path = r"E:\高考志愿填报助手\rawDataFiles\一分一段"

# 请定义最终合并后输出的文件名。
output_file_name = 'merged_all_rankings.csv'


# --- 第二步: 检查路径并查找所有CSV文件 ---

# 检查输入文件夹是否存在
if not os.path.isdir(input_folder_path):
    print(f"错误：文件夹不存在 -> '{input_folder_path}'")
    print("请检查您输入的路径是否正确。")
    exit()

# 使用 glob 模块来查找所有以 '.csv' 结尾的文件
# os.path.join 会智能地处理路径分隔符（'\\' 或 '/'）
csv_files = glob.glob(os.path.join(input_folder_path, "*.csv"))

if not csv_files:
    print(f"警告：在文件夹 '{input_folder_path}' 中没有找到任何CSV文件。")
    exit()

print(f"找到 {len(csv_files)} 个CSV文件，准备开始合并...")


# --- 第三步: 读取并合并文件 ---

# 创建一个空的列表，用于存放从每个CSV文件中读取出来的数据（DataFrame）
all_dataframes = []

# 遍历所有找到的CSV文件路径
for file in csv_files:
    try:
        # 使用pandas读取CSV文件
        df = pd.read_csv(file)
        # 将读取到的数据添加到列表中
        all_dataframes.append(df)
        print(f"  - 已读取: {os.path.basename(file)}")
    except Exception as e:
        print(f"  - 读取文件时出错: {os.path.basename(file)}。错误: {e}")
        # 如果某个文件读取失败，可以选择跳过或停止，这里我们选择跳过
        continue

# 检查是否成功读取了任何数据
if not all_dataframes:
    print("未能成功读取任何CSV文件，程序退出。")
    exit()

# 使用 pd.concat 将列表中的所有DataFrame垂直堆叠合并成一个
# ignore_index=True 会为合并后的新DataFrame创建一个从0开始的连续索引
merged_df = pd.concat(all_dataframes, ignore_index=True)

print("\n所有文件合并完成！")
print(f"合并后的总行数: {len(merged_df)}")


# --- 第四步: 保存合并后的文件 ---

# 构建完整的输出文件路径
output_file_path = os.path.join(input_folder_path, output_file_name)

# 使用 .to_csv() 方法将合并后的数据保存到一个新的CSV文件中
# encoding='utf-8-sig' 是关键！它能确保导出的CSV文件被Excel正确识别中文，避免乱码。
# index=False 表示我们不需要将DataFrame的行号（如0,1,2,3...）也保存到文件中。
merged_df.to_csv(output_file_path, index=False, encoding='utf-8-sig')

print(f"\n成功！合并后的文件已保存到: {output_file_path}")
