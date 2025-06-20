# -*- coding: utf-8 -*-
# 这是一个用于处理原始投档线文件，并将多条专业分数线聚合成一条院校平均线的脚本。
# 功能：按大学名称分组，计算分数和位次的平均值。

import pandas as pd

# --- 第一步: 用户配置 ---

# 您包含三列（大学名称, 分数, 位次）的原始文件名 (可以是 .xlsx 或 .csv)
raw_data_file = '01temp.csv'

# 定义最终输出的文件名
output_file_name = '03temp.csv'

# --- 第二步: 加载数据文件 ---
try:
    # 假设您的文件有列标题，如果没有，请使用 header=None
    # 尝试用'gbk'编码读取，如果失败，可以换成'utf-8'
    if raw_data_file.endswith('.xlsx'):
        df = pd.read_excel(raw_data_file)
    else:
        df = pd.read_csv(raw_data_file, encoding='gbk')

    # 为了代码的健壮性，我们将列名统一为英文
    # 请确保您的文件列名与此对应，或在此处修改
    df.columns = ['school_name', 'score', 'rank']

    print(f"成功读取主数据文件: '{raw_data_file}'")

except FileNotFoundError as e:
    print(f"错误：找不到文件 {e.filename}。请确保文件都在正确的位置。")
    exit()
except Exception as e:
    print(f"读取文件时发生错误: {e}")
    exit()

# --- 第三步: 数据预处理 ---
print("\n正在进行数据预处理...")

# 1. 确保分数和位次列是数字类型，无法转换的脏数据会变成NaN (空值)
df['score'] = pd.to_numeric(df['score'], errors='coerce')
df['rank'] = pd.to_numeric(df['rank'], errors='coerce')

# 2. 删除包含无效分数或位次的行
original_rows = len(df)
df.dropna(subset=['score', 'rank'], inplace=True)
print(f"删除了 {original_rows - len(df)} 行包含无效分数或位次的数据。")

# --- 第四步: 核心聚合逻辑 (Group By) ---
print("开始按学校名称进行分组聚合...")

# 使用 .groupby() 按学校名称对数据进行分组
# 然后对'score'和'rank'这两列计算平均值 (.mean())
# as_index=False 让'school_name'保留为普通列，而不是成为结果的索引
df_aggregated = df.groupby('school_name', as_index=False).agg({
    'score': 'mean',
    'rank': 'mean'
})

print("聚合计算完成！")

# --- 第五步: 格式化输出 ---

# 将计算出的平均分数和位次四舍五入并转换为整数，使其更符合常规认知
df_aggregated['score'] = df_aggregated['score'].round(0).astype(int)
df_aggregated['rank'] = df_aggregated['rank'].round(0).astype(int)

print(f"\n数据处理完成！共聚合得到 {len(df_aggregated)} 所院校的平均数据。")
print("最终数据预览：")
print(df_aggregated.head())

# --- 第六步: 保存最终文件 ---

# 保存到最终的CSV文件
df_aggregated.to_csv(output_file_name, index=False, encoding='utf-8-sig')
print(f"\n成功！聚合后的院校平均分数据已保存为: '{output_file_name}'")

