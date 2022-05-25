
export class BasePackageManger {
    protected installing: boolean;
    protected currentVersionFile: string;

    constructor() {
        this.installing = false;
    }
}