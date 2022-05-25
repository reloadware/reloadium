import * as path from 'path';
import { injectable } from 'inversify';
import { DebugConfiguration, Uri } from 'vscode';
import * as vscode from 'vscode';
import { LaunchJsonReader } from './launchJsonReader';

export namespace Commands {
    export const RUN = 'reloadium.run';
}

@injectable()
export class RunWithReloadium {
    public readonly supportedWorkspaceTypes = { untrustedWorkspace: false, virtualWorkspace: false };
    disposables: any;

    constructor(
        disposables: any,
    ) {
        this.disposables = disposables;
    }

    public activate(): Promise<void> {
        this.disposables.push(
            vscode.commands.registerCommand(Commands.RUN, async (file?: Uri) => {
                const config = await this.getDebugConfiguration(file);
                vscode.debug.startDebugging(undefined, config);
            }),
        );
        return Promise.resolve();
    }

    private async getDebugConfiguration(uri?: Uri): Promise<DebugConfiguration> {
        const launchJsonReader = new LaunchJsonReader();
        const configs = (await launchJsonReader.getConfigurationsByUri(uri!)).filter((c) => c.request === 'launch');
        for (const config of configs) {
            if (config.purpose?.includes("debug-in-terminal")) {
                if (!config.program && !config.module && !config.code) {
                    // This is only needed if people reuse debug-test for debug-in-terminal
                    config.program = uri?.fsPath ?? '${file}';
                }
                // Ensure that the purpose is cleared, this is so we can track if people accidentally
                // trigger this via F5 or Start with debugger.
                config.purpose = [];
                return config;
            }
        }
        return {
            name: `Debug ${uri ? path.basename(uri.fsPath) : 'File'}`,
            type: 'python',
            request: 'launch',
            module: "reloadium",
            console: 'integratedTerminal',
            args : ["run", '${file}'],
            // eslint-disable-next-line @typescript-eslint/naming-convention
            env: {"PYTHONPATH": "/home/q/Code/reloadware/product/"},
        };
        // return {
        //     name: `Debug ${uri ? path.basename(uri.fsPath) : 'File'}`,
        //     type: 'python',
        //     request: 'launch',
        //     program: uri?.fsPath ?? '${file}',
        //     console: 'integratedTerminal',
        // };
    }
}
