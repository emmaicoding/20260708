# 贡献指南

感谢你对幼儿园管理系统项目的关注！本文档说明如何参与项目开发。

## 开发环境

- **JDK 8+**
- **MySQL 8.0+**
- **MySQL Connector/J** — 放入 `lib/` 目录

## 快速开始

```bash
# 1. 克隆仓库
git clone https://github.com/egg-rolls/20260708.git
cd 20260708

# 2. 配置数据库（编辑 src/kindergarten/util/DBUtil.java 中的账号密码）

# 3. 编译运行
bash build.sh    # 编译
bash run.sh      # 运行
bash test.sh     # 测试
```

## 分支策略

```
main          ← 稳定版本，保护分支
  └── dev     ← 开发主线
       ├── feature/xxx   ← 新功能
       ├── fix/xxx       ← Bug 修复
       └── refactor/xxx  ← 重构
```

- **main** — 生产就绪代码，仅通过 PR 合入
- **dev** — 开发集成分支，功能完成后合入
- **feature/** — 新功能开发，从 dev 创建
- **fix/** — Bug 修复，从 dev 创建

## 提交规范

遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

| 类型 | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `refactor` | 重构（不改变功能） |
| `docs` | 文档更新 |
| `style` | 代码格式调整 |
| `test` | 测试相关 |
| `chore` | 构建/工具链变更 |

### 示例

```
feat(child): 新增幼儿信息批量导入功能

- 支持 CSV 格式导入
- 自动校验数据格式
- 重复数据跳过并记录

Closes #12
```

## 代码规范

- 每个类必须有类级别注释（作者、日期、功能、版本）
- 使用 `PreparedStatement`，禁止字符串拼接 SQL
- DAO 层不直接打印输出，通过 `DataAccessException` 传播异常
- 密码使用 `PasswordUtil.hash()` 哈希存储

## Pull Request 流程

1. 从 `dev` 创建功能分支
2. 完成开发后推送分支
3. 创建 PR，填写 PR 模板
4. 至少 1 人 Code Review
5. CI 通过后合入 `dev`
6. 版本发布时 `dev` → `main`

## 报告 Bug

使用 [Bug Report](https://github.com/egg-rolls/20260708/issues/new?template=bug_report.md) 模板，包含：

- 复现步骤
- 期望行为 vs 实际行为
- 环境信息（JDK 版本、MySQL 版本、操作系统）
