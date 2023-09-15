# Flappy Monorepo

[English](./README.md) | 简体中文 | [日本語](./README.ja.md)

本 Monorepo 包含了使用不同的编程语言实现的 Flappy 库。Flappy 是一个致力于简化开发基于大语言模型（LLMs）的 AI 应用和 AI Agent 的门槛的库。

## 概述

Flappy 过深思熟虑的设计，以满足各行各业对基于 LLM 的 AI 应用的日益增长的需求。我们的目标是通过管理LLM输入和输出来确保数据的**可控性**、一致性和机器可读性。

Flappy 独特的功能是能够将抽象类转换为 JSON Schema Schema。开发者可以用他们熟悉的任何编程语言（如Java，Python，PHP或TypeScript）定义这些类。

Flappy 会对 LLM 的输出进行抽象语法树（AST）解析，以确保生成的 JSON 数据严格遵循定义的 JSON Schema Schema。通过将编程语言的灵活性与 JSON Schema 的标准化相结合，Flappy 将 AI 应用开发推向了新的可靠性和效率的水平。

Flappy 还提供了类似于 OpenAI 的代码解释器的功能。它在**生产环境就绪的安全沙箱**中执行由 LLM 产生的 Python 或 TypeScript 代码。这种策略为 AI 生成的代码执行提供了安全的环境，最大限度地减少了运行时错误和可能的安全问题。

最后，Flappy 为不同的 LLM 提供了一个抽象层，允许用户**轻松切换不同的 LLM**。开发者还可以指定备用的 LLM，以确保应用的健壮性。

## 入门

要开始使用 Flappy，请从下面的列表中选择你所需要的语言实现：

- [Node.js](./packages/nodejs/README.md)
- [Python](./packages/python/README.md)
- PHP（即将推出）
- Java（即将推出）
- C#（即将推出）
- Go（即将推出）

## 贡献

我们非常欢迎社区的贡献！ 更多详情，请参考我们的[贡献指南](./CONTRIBUTING.md)。

## 许可证

本项目采用 [Apache 2.0 许可证](./LICENSE)。Copyright (c) 2023 Pleisto Inc.
