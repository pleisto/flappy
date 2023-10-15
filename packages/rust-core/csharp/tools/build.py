#!/usr/bin/env python3

# Forked from https://github.com/apache/incubator-opendal/blob/main/bindings/java/tools/build.py

from argparse import ArgumentDefaultsHelpFormatter, ArgumentParser
from pathlib import Path
import shutil
import subprocess


def classifier_to_target(classifier: str) -> str:
    if classifier == "osx-aarch_64":
        return "aarch64-apple-darwin"
    if classifier == "osx-x86_64":
        return "x86_64-apple-darwin"
        
    if classifier == "linux-x86_64":
        return "x86_64-unknown-linux-gnu"
    if classifier == "linux-aarch64":
        return "aarch64-unknown-linux-gnu"
    
    if classifier == "windows-x86_64":
        return "x86_64-pc-windows-msvc"

    raise Exception(f"Unsupported classifier: {classifier}")


def get_cargo_artifact_name(classifier: str) -> str:
    if classifier == "osx-aarch_64" or classes == "osx-x86_64":
        return "libflappy_csharp_bindings.dylib"
    if classifier == "linux-x86_64" or classifier == "linux-aarch64":
        return "libflappy_csharp_bindings.so"
    if classifier == "windows-x86_64":
        return "flappy_csharp_bindings.dll"
    raise Exception(f"Unsupported classifier: {classifier}")


if __name__ == "__main__":
    basedir = Path(__file__).parent.parent

    parser = ArgumentParser(formatter_class=ArgumentDefaultsHelpFormatter)
    parser.add_argument("--classifier", type=str, required=True)
    parser.add_argument("--target", type=str, default="")
    parser.add_argument("--profile", type=str, default="dev")
    args = parser.parse_args()

    cmd = ["cargo", "build", "--color=always", f"--profile={args.profile}"]

    if args.target:
        target = args.target
    else:
        target = classifier_to_target(args.classifier)

    command = ["rustup", "target", "add", target]
    print("$ " + subprocess.list2cmdline(command))
    subprocess.run(command, cwd=basedir, check=True)
    cmd += ["--target", target]

    output = basedir / "target" / "bindings"
    Path(output).mkdir(exist_ok=True, parents=True)
    cmd += ["--target-dir", output]

    print("$ " + subprocess.list2cmdline(cmd))
    subprocess.run(cmd, cwd=basedir, check=True)

    # History reason of cargo profiles.
    profile = "debug" if args.profile in ["dev", "test", "bench"] else args.profile
    artifact = get_cargo_artifact_name(args.classifier)
    src = output / target / profile / artifact
    dst = basedir / "target" / "classes" / "native" / args.classifier / artifact
    dst.parent.mkdir(exist_ok=True, parents=True)
    shutil.copy2(src, dst)