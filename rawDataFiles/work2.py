# -*- coding: utf-8 -*-
# 这是一个用于批量处理文件夹内所有投档线CSV文件，并最终合并成一个文件的“超级脚本”。
# v2更新: 增加了对min_rank和min_score列中空值的处理，防止导入数据库时出错。

import os
import glob
import pandas as pd

# --- 第一步: 用户配置 (请在这里修改) ---

# 请输入包含所有原始投档线CSV文件的文件夹路径。
input_folder_path = r"E:\高考志愿填报助手\rawDataFiles\投档线"

# 请输入您的学校ID对照表 `school_map.csv` 的完整路径。
# 如果它和本脚本在同一个文件夹，可以直接写 'school_map.csv'。
school_map_file = 'school_map.csv'

# 请定义最终合并后输出的文件名。
output_file_name = 'merged_admission_scores_final.csv'

# --- 第二步: 预加载对照表和查找文件 ---

try:
    # 预先加载学校ID对照表，避免在循环中重复读取
    df_school_map = pd.read_csv(school_map_file)
    df_school_map.columns = ['school_id_new', 'school_name_map']
    print(f"成功预加载学校对照表: '{school_map_file}'")
except FileNotFoundError:
    print(f"错误：找不到学校对照表 '{school_map_file}'。请检查路径和文件名。")
    exit()

# 查找所有待处理的CSV文件
if not os.path.isdir(input_folder_path):
    print(f"错误：输入文件夹不存在 -> '{input_folder_path}'")
    exit()
csv_files = glob.glob(os.path.join(input_folder_path, "*.csv"))
if not csv_files:
    print(f"警告：在文件夹 '{input_folder_path}' 中没有找到任何CSV文件。")
    exit()

print(f"\n找到 {len(csv_files)} 个投档线文件，准备开始批处理...")

# --- 第三步: 核心处理循环 ---

# 创建一个空列表，用于存放从每个文件处理好的数据
all_processed_data = []

# 遍历所有找到的CSV文件
for file_path in csv_files:
    file_name = os.path.basename(file_path)
    print(f"\n--- 正在处理文件: {file_name} ---")

    try:
        # 1. 读取单个投档线文件
        df_scores = pd.read_csv(file_path, encoding='gbk')
        original_columns = df_scores.columns.tolist()

        # 2. 清理学校名称（空格和括号）
        df_scores['school_id'] = df_scores['school_id'].str.replace(' ', '', regex=False)
        df_scores['school_id'] = df_scores['school_id'].str.replace('（', '(', regex=False)
        df_scores['school_id'] = df_scores['school_id'].str.replace('）', ')', regex=False)

        # 3. 删除文件内部的重复学校记录
        df_scores.drop_duplicates(subset=['school_id'], keep='first', inplace=True)

        # 4. 与学校地图进行匹配
        df_merged = pd.merge(
            df_scores, df_school_map,
            left_on='school_id',
            right_on='school_name_map',
            how='left'
        )

        # 5. 删除未能匹配的行
        original_count = len(df_merged)
        df_merged.dropna(subset=['school_id_new'], inplace=True)
        if original_count > len(df_merged):
            print(f"  - 发现并删除了 {original_count - len(df_merged)} 行未匹配到的学校记录。")

        # 6. 整理列
        if not df_merged.empty:
            df_merged['school_id'] = df_merged['school_id_new']
            df_final_single = df_merged[original_columns].copy()

            # 【核心修正】处理min_rank和min_score中的空值
            # 使用0填充空值，以确保后续可以转换为整数/数字类型
            # 假设列名为 'min_rank' 和 'min_score'，如果不是请在这里修改
            if 'min_rank' in df_final_single.columns:
                df_final_single['min_rank'].fillna(0, inplace=True)
            if 'min_score' in df_final_single.columns:
                df_final_single['min_score'].fillna(0, inplace=True)

            # 确保ID和位次列是整数类型
            df_final_single['school_id'] = df_final_single['school_id'].astype(int)
            if 'min_rank' in df_final_single.columns:
                df_final_single['min_rank'] = df_final_single['min_rank'].astype(int)

            # 7. 将处理好的数据添加到总列表中
            all_processed_data.append(df_final_single)
            print(f"  - 处理完成，获得 {len(df_final_single)} 条有效记录。")
        else:
            print("  - 处理后无有效数据，已跳过。")

    except Exception as e:
        print(f"  - 处理文件 {file_name} 时发生错误: {e}")
        continue

# --- 第四步: 合并所有结果并保存 ---

if not all_processed_data:
    print("\n所有文件处理完毕，但未能生成任何有效数据。程序退出。")
    exit()

print("\n所有文件处理完毕，正在合并结果...")
# 使用 pd.concat 将列表中的所有数据垂直合并成一个
final_merged_df = pd.concat(all_processed_data, ignore_index=True)

# 构建完整的输出文件路径
output_file_path = os.path.join(os.path.dirname(input_folder_path), output_file_name)

# 保存到最终的CSV文件
final_merged_df.to_csv(output_file_path, index=False, encoding='utf-8-sig')

print("\n==============================================")
print(f"恭喜！批处理完成！")
print(f"合并后的总记录数: {len(final_merged_df)}")
print(f"最终文件已保存到: {output_file_path}")
print("==============================================")
