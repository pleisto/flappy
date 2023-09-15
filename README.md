# Flappy Monorepo

> :warning: **This project is still under development.** We're working hard to release the first version of Flappy as soon as possible. Stay tuned! Documentation and code examples will be available soon.

English | [简体中文](./README.zh-Hans.md) | [日本語](./README.ja.md)

This monorepo gathers all the Flappy libraries, each implemented in a different programming language. Flappy is a library developed to streamline the creation of AI applications and agents that use Large Language Models (LLMs).

## Overview

Flappy is thoughtfully designed to meet the increasing demand for LLM-based AI applications across various sectors. Our goal is to offer a library that manages the inputs and outputs of LLMs, ensuring **controlled**, consistent, and machine-readable data.

A unique feature of Flappy is its capability to transform abstract classes into JSON Schema schemas. The user can define these classes in their preferred programming language, such as Java, Python, PHP, or TypeScript. These definitions include type and function signatures.

To enhance machine readability and maintain interoperability, Flappy performs AST (Abstract Syntax Tree) parsing on the LLM outputs. This process ensures that the generated JSON data strictly adheres to the defined JSON Schema schema. By integrating the flexibility of programming languages with the standardization of JSON Schema, Flappy propels AI application development to new levels of reliability and efficiency.

Flappy also offers features similar to OpenAI's Code Interpreter. It executes the Python or TypeScript code produced by LLMs in a **production-ready secure sandbox**. This strategy provides a safe environment for the execution of AI-generated code, minimizing the risk of runtime errors and potential security issues.

Lastly, Flappy provides an abstraction layer for different LLMs, allowing users to **easily switch between different LLMs**. Users can also designate fallback LLMs to ensure their applications' stability.

## Getting Started

To begin with Flappy, select your preferred language implementation from the list below:

- [Node.js](./packages/nodejs/README.md)
- Python (coming soon)
- PHP (coming soon)
- Java (coming soon)
- C# (coming soon)
- Go (coming soon)

## Contributing

We're thrilled to receive contributions from the community! For more details, please refer to our [contributing guidelines](./CONTRIBUTING.md).

## License

This project is covered under the [Apache License 2.0](./LICENSE). Copyright (c) 2023 Pleisto Inc.
